Given /^the Haml fragment "([^"]*)"$/ do |haml|
  @nate = Nate::Engine.new haml, :haml
end