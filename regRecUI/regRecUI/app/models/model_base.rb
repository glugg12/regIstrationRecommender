class ModelBase
  include ActiveModel::Serializers::JSON
  include ActiveModel::Model
  include ActiveModel::Validations::Callbacks
end