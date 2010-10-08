Feature:

  As a rubyist using Nate
  I want to be able to use Haml instead of HTML for my templates
  
  Scenario: Haml content should work with nate
    Given the Haml fragment "%strong{:class => 'code', :id => 'message'} Hello, World!"
      When { 'strong' => 'Goodbye, World!' } is injected
      Then the plain HTML fragment is <strong class="code" id="message">Goodbye, World!</strong>
      