Feature:

 As someone working on a dynamic website
 In order to keep the HTML free from logic
 I want 'nate' to transform plain HTML based on data I supply

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
      | data                             | transformed                                                           |
      | {'.section' => [ 'Section 1', 'Section 2' ] } | <div class="section">Section 1</div><div class="section">Section 2</div>                                |
      | {'.content' => [ 'Content 1', 'Content 2' ]}  | <div class="section"><span class="content">Content 1</span><span class="content">Content 2</span></div> |

