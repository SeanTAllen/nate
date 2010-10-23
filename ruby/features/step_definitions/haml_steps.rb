Given /^the Haml fragment "([^"]*)"$/ do |haml|
  @nate_states.push( Nate::Engine.new haml, :haml )
end