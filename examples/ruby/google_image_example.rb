require File.dirname(__FILE__) + "/create_browser_driver"

  include CreateBrowserDriver  
  def run_scenario(options)
    create_selenium_driver("http://images.google.com")
    browser.start_new_browser_session
    puts "Testing #{browser.browser_url} on #{browser.browser_string} "
    browser.open "/"
    browser.location.should match(%r{http://images.google.com/})
    puts "Typing the search string #{options[:search_string]} into the search images text field"
    browser.type "q", options[:search_string]
    puts "Clicking the search button and waiting for the page to load"
    browser.click "btnG"
    browser.wait_for_page_to_load "30000"
    puts "Click the link to refine the search to large images"
    browser.click "css=a:contains(\"Large\")"
    browser.wait_for_element("css=li:contains(\"Large\")")
    puts "Verify that the size of large is now selected"
    browser.element?("css=li:contains(\"Large\")").should == true
    puts "Verifying that #{options[:search_string].split(/ /).first} is on the page"
    browser.text?(options[:search_string].split(/ /).first).should be_true
    browser.close_current_browser_session
  end
  

