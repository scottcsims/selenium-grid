require File.dirname(__FILE__) + "/create_browser_driver"

  include CreateBrowserDriver  
  def run_scenario(options)
    create_selenium_driver("http://images.google.com")
    browser.start_new_browser_session
    puts "Testing #{browser.browser_url} on #{browser.browser_string} "
    browser.open "/"
    page.location.should match(%r{http://images.google.com/})
    puts "Typing the search string #{options[:search_string]} into the search images text field"
    page.type "q", options[:search_string]
    puts "Clicking the search button and waiting for the page to load"
    page.click "btnG"
    browser.wait_for_page_to_load "30000"
    puts "Clicking show options"
    page.click "rptgl"
    puts "Clicking \"Large\" for the image size"
    browser.click "imgsz_l"
    puts "Clicking \"Photo\" for the image type"
    page.click "imgtype_photo"
    puts "Clicking the search button and waiting for the page to load"
    page.click "btnG", :wait_for => :page
    puts "Verifying that #{options[:search_string].split(/ /).first} is on the page"
    page.text?(options[:search_string].split(/ /).first).should be_true
    browser.close_current_browser_session
  end
  

