module SessionHelper
  NODE_NAME = :node_name
  EDGE = :edge
  REG_PIECE = :reg_piece
  NODE_PAIRING= :node_pairing
  NODE_ID = :node_id

  RECOMMENDATIONS = :recommendations

  def set_node_name(node_name)
    session[NODE_NAME] = node_name
  end

  def get_node_name
    session[NODE_NAME]
  end

  def set_edge(edge)
    session[EDGE] = edge
  end

  def get_edge
    session[EDGE]
  end

  def set_reg_piece(reg_piece)
    session[REG_PIECE] = reg_piece
  end

  def get_reg_piece
    session[REG_PIECE]
  end

  def set_node_pairing(node1, node2)
    session[NODE_PAIRING] = { "node1": node1, "node2": node2 }
  end

  def get_node_pairing
    session[NODE_PAIRING]
  end

  def set_node_id(node_id)
    session[NODE_ID] = node_id
  end

  def get_node_id
    session[NODE_ID]
  end

  def set_recommendations(rec)
    session.clear
    session[RECOMMENDATIONS] = rec.first(75)
  end

  def get_recommendations
    session[RECOMMENDATIONS]
  end
end