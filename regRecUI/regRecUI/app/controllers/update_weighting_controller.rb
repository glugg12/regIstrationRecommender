class UpdateWeightingController < ApplicationController
  def start
    @user_response = UpdateWeight.new
    response = ApiService.get_all_nodes
    if response.status.eql?200
      all_nodes = JSON.parse(response.body)
    else
      redirect_to api_error_path and return
    end

    @node1 = all_nodes.sample
    @node2 = all_nodes.sample

    while @node1["id"].eql?@node2["id"]
      @node2 = all_nodes.sample
    end
    set_node_pairing(@node1, @node2)
  end

  def submit
    @user_response = UpdateWeight.new(update_weighting_params)
    if @user_response.response.to_i.eql?0
      redirect_to update_weight_start_path and return
    end
    nodes = get_node_pairing
    response = ApiService.get_check_edge_pairing_exists(nodes["node1"]["id"], nodes["node2"]["id"])
    if response.status.eql?200
      edge = JSON.parse(response.body)
    elsif response.status.eql?404
      check = JSON.parse(response.body)
      if check["title"].eql?"EDGE_NOT_FOUND"
        #create edge for pair
        response = ApiService.post_add_edge(nodes["node1"]["id"], nodes["node2"]["id"])
        unless response.status.eql? 200
          redirect_to api_error_path and return
        end
        edge = JSON.parse(response.body)
      else
        redirect_to api_error_path and return
      end
    else
      redirect_to api_error_path and return
    end

    response = ApiService.post_update_edge_weight(edge["id"], @user_response.response.to_i)
    if response.status.eql?200
      redirect_to update_weight_start_path
    else
      redirect_to api_error_path
    end
  end

  def reg_start
    @all_nodes = nil
    response = ApiService.get_all_nodes
    if response.status.eql?200
      @all_nodes = JSON.parse(response.body)
    else
      redirect_to api_error_path
    end
  end

  def reg_piece_weighting
    node_id = params[:node]
    set_node_id(node_id)
    response = ApiService.get_node_by_id(node_id)
    if response.status.eql?200
      @node = JSON.parse(response.body)
    else
      redirect_to api_error_path and return
    end
    @user_input = UpdateWeight.new
    response = ApiService.get_node_reg_pieces(node_id)
    if response.status.eql?200
      reg_pieces = JSON.parse(response.body)
    else
      redirect_to api_error_path and return
    end
    @reg_piece = reg_pieces.sample
    set_reg_piece(@reg_piece)
  end

  def submit_reg_weight
    reg_piece = get_reg_piece
    @user_input = UpdateWeight.new(update_weighting_params)

    if @user_input.response.to_i.eql?0
      redirect_to controller: 'update_weighting', action: 'reg_piece_weighting', :node => get_node_id  and return
    end
    response = ApiService.post_update_reg_weight(reg_piece["id"], @user_input.response.to_i)

    if response.status.eql?200
      redirect_to controller: 'update_weighting', action: 'reg_piece_weighting', :node => get_node_id
    else
      redirect_to api_error_path
    end
  end

  private

  def update_weighting_params
    params.require(:update_weight).permit(:response)
  end
end