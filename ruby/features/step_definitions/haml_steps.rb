Given /^the Haml fragment "([^"]*)"$/ do |haml|
  @nate = Nate::HamlTemplate.new haml
end