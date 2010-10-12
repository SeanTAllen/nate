Given /^the Haml fragment "([^"]*)"$/ do |haml|
  @nate = Nate::HamlEngine.new haml
end