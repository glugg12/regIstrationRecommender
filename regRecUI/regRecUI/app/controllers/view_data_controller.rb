class ViewDataController < ApplicationController
  def start
    @all_nodes = nil
    response = ApiService.get_all_nodes
    if response.status.eql?200
      @all_nodes = JSON.parse(response.body)
    else
      redirect_to api_error_path
    end
  end

  def view_node
    node = params[:node]
    response = ApiService.get_node_by_id(node)
    if response.status.eql?200
      @node = JSON.parse(response.body)
    else
      redirect_to api_error_path
    end

    response = ApiService.get_node_get_relations(node)
    if response.status.eql?200
      @node_relations = JSON.parse(response.body)
    else
      redirect_to api_error_path
    end

    response = ApiService.get_node_reg_pieces(node)
    if response.status.eql?200
      @node_reg = JSON.parse(response.body)
    else
      redirect_to api_error_path
    end
  end

  private

  def select_view_node_params
    params.require(:select_view_node).permit(:nodeId)
  end
end