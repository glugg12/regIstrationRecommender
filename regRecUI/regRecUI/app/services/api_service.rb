class ApiService
  require 'json'
  def self.post_add_node(node_name)
    body = { "name": node_name }
    response = Faraday.post("#{API_HOST}/element-node", body.to_json, get_headers)
    response
  end

  def self.get_all_nodes
    response = Faraday.get("#{API_HOST}/element-node")
    response
  end

  def self.post_add_edge(node1, node2)
    body = { "node1": node1, "node2": node2 }
    response = Faraday.post("#{API_HOST}/edge", body.to_json, get_headers)
    response
  end

  def self.post_add_reg_piece(content, parent)
    body = { "content": content, "parentNodeId": parent}
    response = Faraday.post("#{API_HOST}/RegPiece", body.to_json, get_headers)
    response
  end

  def self.get_node_by_id(node_id)
    response = Faraday.get("#{API_HOST}/element-node/#{node_id}")
    response
  end

  def self.get_node_get_relations(node_id)
    response = Faraday.get("#{API_HOST}/element-node/#{node_id}/nodeList")
    response
  end

  def self.get_node_reg_pieces(node_id)
    response = Faraday.get("#{API_HOST}/element-node/#{node_id}/regPieces")
    response
  end

  def self.get_check_edge_pairing_exists(node1, node2)
    params = { "node1": node1, "node2": node2}
    response = Faraday.get("#{API_HOST}/edge/pairing", params)
    response
  end

  def self.post_update_edge_weight(edge_id, weight)
    body = { "weighting": weight }
    response = Faraday.post("#{API_HOST}/edge/#{edge_id}/weighting", body.to_json, get_headers)
    response
  end

  def self.post_update_reg_weight(reg_id, weight)
    body = { "weighting": weight }
    response = Faraday.post("#{API_HOST}/RegPiece/#{reg_id}/weighting", body.to_json, get_headers)
    response
  end

  def self.get_recommendations_v1(node_id)
    params ={ "nodeId": node_id }
    response = Faraday.get("#{API_HOST}/recommend", params)
    response
  end

  def self.get_recommendations_v2(node_id, dest_id)
    params ={ "nodeId": node_id, "destId": dest_id }
    response = Faraday.get("#{API_HOST}/recommend/dijkstra", params)
    response
  end

  private
  def self.get_headers
    { 'Content-Type': "application/json" }
  end
end