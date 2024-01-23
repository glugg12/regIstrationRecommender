Rails.application.routes.draw do
  # Define your application routes per the DSL in https://guides.rubyonrails.org/routing.html

  # Reveal health status on /up that returns 200 if the app boots with no exceptions, otherwise 500.
  # Can be used by load balancers and uptime monitors to verify that the app is live.
  get "up" => "rails/health#show", as: :rails_health_check

  get "/", to: "home#start", as: 'home'
  get "/add_data", to: "add_data#start", as: 'add_data_start'
  get "/add_data/node", to: "add_data#node_start", as: 'add_node_start'
  post "/add_data/node", to: "add_data#submit_node", as: 'submit_node'
  get "/add_data/node/success", to: "add_data#submit_node_success", as: 'submit_node_success'
  get "/add_data/edge", to: "add_data#edge_start", as: 'add_edge_start'
  post "/add_data/edge", to: "add_data#submit_add_edge", as: 'add_edge_submit'
  get "/add_data/edge/success", to: "add_data#add_edge_success", as: 'add_edge_success'
  get "/add_data/reg_piece", to: "add_data#reg_piece_start", as: 'add_reg_piece_start'
  post "/add_data/reg_piece", to: "add_data#submit_reg_piece", as: 'add_reg_submit'
  get "/add_data/reg_piece/success", to: "add_data#add_reg_piece_success", as: 'add_reg_success'

  get "/view_data", to: "view_data#start", as: 'view_data_home'
  get "/view_data/node", to: "view_data#view_node", as: 'view_node_page'

  get "/update_weighting", to: "update_weighting#start", as: 'update_weight_start'
  post "/update_weighting", to: "update_weighting#submit", as: 'update_weight_submit'

  get "/update_reg_weighting", to: "update_weighting#reg_start", as: 'update_reg_weight_start'
  get "/update_reg_weighting/node", to: "update_weighting#reg_piece_weighting", as: 'update_reg_node_selected'
  post "/update_reg_weighting/node", to: "update_weighting#submit_reg_weight", as: 'update_reg_weight_submit'

  get "/recommendations_v1", to: "recommendation#start_v1", as: 'recommendations_v1'
  get "/recommendations_v1/view", to: "recommendation#recommend_v1", as: 'recommendations_v1_view'

  get "/recommendations_v2", to: "recommendation#start_v2", as: 'recommendations_v2'
  post "/recommendations_v2", to: "recommendation#recommend_v2", as: 'recommendations_v2_submit'
  get "/recommendations_v2/view", to: "recommendation#recommend_v2_view", as: 'recommendations_v2_view'

  get "/error/api", to: "error#api_error", as: 'api_error'


end
