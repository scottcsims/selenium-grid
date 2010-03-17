require "rubygems"
require "rake"

gem "rspec", "=1.1.12"
require "spec"

gem "selenium-client", ">=1.2.18"
require "selenium/client"

require File.expand_path(File.dirname(__FILE__) + "/google_image_example")
