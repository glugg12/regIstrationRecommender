class RecommendationController < ApplicationController
  def start_v1
    @all_nodes = nil
    response = ApiService.get_all_nodes
    if response.status.eql?200
      @all_nodes = JSON.parse(response.body)
    else
      redirect_to api_error_path
    end
  end

  def recommend_v1
    node_id = params[:node]
    response = ApiService.get_recommendations_v1(node_id)
    if response.status.eql?200
      @recommendations = JSON.parse(response.body)
    else
      redirect_to api_error_path
    end
  end

  def start_v2
    @all_nodes = nil
    response = ApiService.get_all_nodes
    if response.status.eql?200
      @all_nodes = JSON.parse(response.body)
    else
      redirect_to api_error_path
    end

    @choose_topic = ChooseTopic.new
  end

  def recommend_v2
    @choose_topic = ChooseTopic.new(choose_topic_params)
    response = ApiService.get_recommendations_v2(@choose_topic.topic1, @choose_topic.topic2)
    if response.status.eql?200
      set_recommendations(JSON.parse(response.body))
      redirect_to recommendations_v2_view_path
    else
      redirect_to api_error_path
    end
  end

  def recommend_v2_view
    @recommendations_v2 = get_recommendations
  end

  private

  def choose_topic_params
    params.require(:choose_topic).permit(:topic1, :topic2)
  end
end