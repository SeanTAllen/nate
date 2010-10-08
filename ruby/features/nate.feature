Feature:

 As someone working on a dynamic website
 In order to keep the HTML free from logic
 I want 'nate' to transform plain HTML based on data I supply

  Scenario: empty data results in no change to the html
    Given the plain HTML fragment "<ul><li></li></ul>"
      When {} is injected
      Then the plain HTML fragment is <ul><li></li></ul>
 
  Scenario: nil value shouldn't modify matched element
    Given the plain HTML fragment "<h1>Header</h1>"
      When { 'h1' => nil } is injected
      Then the plain HTML fragment is <h1>Header</h1>
            
  Scenario Outline: match and inject a single data value
    Given the plain HTML fragment "<div class='section'><span class='content'></span></div>"
     When <data> is injected
     Then the plain HTML fragment is <transformed>
    
    Examples:
      | data                             | transformed                                                           |
      | {'.section' => 'Hello Section' } | <div class="section">Hello Section</div>                              |
      | {'.content' => 'Hello Content'}  | <div class="section"><span class="content">Hello Content</span></div> |

  Scenario Outline: match and inject multiple data values 
    Given the plain HTML fragment "<div class='section'><span class='content'></span></div>"
      When <data> is injected
      Then the plain HTML fragment is <transformed>
      
    Examples:
      | data                                          | transformed                                             |
      | {'.section' => [ 'Section 1', 'Section 2' ] } | <div class="section">Section 1</div><div class="section">Section 2</div>                                |
      | {'.content' => [ 'Content 1', 'Content 2' ]}  | <div class="section"><span class="content">Content 1</span><span class="content">Content 2</span></div> |
      
  Scenario Outline: match and inject values into a subselection of matched html
    Given the plain HTML fragment "<div class='section'><span class='greeting'></span></div>"
      When <data> is injected
      Then the plain HTML fragment is <transformed>
    
    Examples:
      | data                                         | transformed |
      | { '.section' => { '.greeting' => 'Hello' } } | <div class="section"><span class="greeting">Hello</span></div> |
      | { '.section' => { 'span' => 'Hello' } }      | <div class="section"><span class="greeting">Hello</span></div> |
      
  Scenario Outline: match and inject multiple data values into a subselection of matched html
    Given the plain HTML fragment "<div class='section'><span class='greeting'></span></div>"
      When <data> is injected
      Then the plain HTML fragment is <transformed>
      
    Examples:
      | data                                                                           | transformed |
      | { '.section' => [ { '.greeting' => 'Hello' }, { '.greeting' => 'Goodbye' } ] } | <div class="section"><span class="greeting">Hello</span></div><div class="section"><span class="greeting">Goodbye</span></div> |
      | { '.section' => [ { 'span' => 'Hello' }, { 'span' => 'Goodbye' } ] }           | <div class="section"><span class="greeting">Hello</span></div><div class="section"><span class="greeting">Goodbye</span></div> |
      
  Scenario: match and inject data into element attributes
    Given the plain HTML fragment "<a href='#'>my link</a>"
      When { 'a' => { 'href' => 'http://www.example.com' } } is injected
      Then the plain HTML fragment is <a href="http://www.example.com">my link</a>

  Scenario: non-existent attributes on an element should be ignored
    Given the plain HTML fragment "<h1>Header</h1>"
      When { 'a' => { 'style' => 'http://www.example.com' } } is injected
      Then the plain HTML fragment is <h1>Header</h1>
      
  Scenario: when doing an attribute match, special 'content' attribute should change the inner_html
    Given the plain HTML fragment "<a href='#'>my link</a>"
      When { 'a' => { 'href' => 'http://www.example.com', 'content' => 'example.com' } } is injected
      Then the plain HTML fragment is <a href="http://www.example.com">example.com</a>
