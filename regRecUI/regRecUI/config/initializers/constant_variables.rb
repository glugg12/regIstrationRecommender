# frozen_string_literal: true
API_HOST = "http://#{ENV['API_HOST']}" unless ENV['API_HOST'].blank?
API_HOST = 'http://localhost:8080' unless ENV['API_HOST'].present?