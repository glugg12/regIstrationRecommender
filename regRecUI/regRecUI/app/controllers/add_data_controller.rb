class AddDataController < ApplicationController
  def start

  end

  def node_start
    @node = AddNode.new
  end

  def submit_node
    @node = AddNode.new(add_node_params)
    response = ApiService.post_add_node(@node.name)
    if response.status.eql?200
      set_node_name(@node.name)
      redirect_to submit_node_success_path
    else
      redirect_to api_error_path
    end
  end

  def submit_node_success
    @add_data_link = add_data_start_path
    @node_name = get_node_name
  end

  def edge_start
    response = ApiService.get_all_nodes
    @all_nodes = nil
    @add_edge = AddEdge.new
    if response.status.eql?200
      @all_nodes = JSON.parse(response.body)
    else
      redirect_to api_error_path
    end
  end

  def submit_add_edge
    @add_edge = AddEdge.new(add_edge_params)
    response = ApiService.post_add_edge(@add_edge.nodeId1, @add_edge.nodeId2)
    if response.status.eql?200
      set_edge(JSON.parse(response.body))
      redirect_to add_edge_success_path
    else
      redirect_to api_error_path
    end
  end

  def add_edge_success
    @add_data_link = add_data_start_path
    @edge = get_edge
  end

  def reg_piece_start
    @add_reg_piece = AddRegPiece.new
    @all_nodes = nil
    response = ApiService.get_all_nodes
    if response.status.eql?200
      @all_nodes = JSON.parse(response.body)
    else
      redirect_to api_error_path
    end
  end

  def submit_reg_piece
    @add_reg_piece = AddRegPiece.new(add_reg_piece_params)
    response = ApiService.post_add_reg_piece(@add_reg_piece.content, @add_reg_piece.parentNodeId)
    if response.status.eql?200
      set_reg_piece(JSON.parse(response.body))
      redirect_to add_reg_success_path
    else
      redirect_to api_error_path
    end
  end

  def add_reg_piece_success
    @add_data_link = add_data_start_path
    @reg_piece = get_reg_piece
  end

  private

  def add_node_params
    params.require(:add_node).permit(:name)
  end

  def add_edge_params
    params.require(:add_edge).permit(:nodeId1, :nodeId2)
  end

  def add_reg_piece_params
    params.require(:add_reg_piece).permit(:content, :parentNodeId)
  end
end