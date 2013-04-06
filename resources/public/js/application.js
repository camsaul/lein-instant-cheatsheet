function getQuery()
{
  var regex = new RegExp("\\?([\\w-?!*]+)$");
  var results = regex.exec(window.location.search);
  if(results == null)
    return "";
  else
    return decodeURIComponent(results[1]);
}

$(document).ready(function() { 
  $('div.fn a').tooltip();

  var filterBox = $('input#filter');
  var namespaceDivs = $('div.namespace');
  var sourceBox = $('div#source');
  var fnDivs = $('div.fn');

  namespaceDivs.each(function() {
    if ($(this).children('div.fn').length == 0) {
      $(this).remove();
    };
  });

  function sourceBoxHtmlForChild(aChild) {
    var returnStr = aChild.attr('data-original-title');
    
    var sourceStr = aChild.attr('source');
    if (sourceStr && sourceStr.length > 0) {
      returnStr += sourceStr;
    }
    
    returnStr += "<hr />"

    return returnStr;
  }

  function filter() {
    var filterStr = filterBox.val();
    
    if (filterStr && filterStr.trim().length) {
      var sourceBoxHtml = '';
      
      fnDivs.each(function() {
	var aChild = $(this).children('a:first-child');

	// regular expressions are a little too slow for the filtering. 
	// Use these lines instead if you'd like to use regexes anyway
	// var regex = new RegExp(filterStr);
	// var results = regex.exec(aChild.text());
	// if (results != null) {

	if (aChild.text().indexOf(filterStr) != -1) {
	  $(this).show()
	  sourceBoxHtml += sourceBoxHtmlForChild(aChild);
	} else {
	  $(this).hide();
	}	
      });
      
      namespaceDivs.each(function() {
       	$(this).toggle($(this).children('div.fn:visible').length != 0);
      });
      
      sourceBox.html(sourceBoxHtml);
      sourceBox.children('a.fn-source').tooltip();
      
    } else {
      namespaceDivs.show();
      fnDivs.show();
      sourceBox.html(null);
    }
  }

  filterBox.keyup(filter);

  $('div.fn').click(function() {
    var aFn = $(this).children('a:first-child');
    sourceBox.html(sourceBoxHtmlForChild(aFn));
    sourceBox.children('a.fn-source').tooltip();
  });

  var initialQuery = getQuery();
  if (initialQuery && initialQuery.length) {
    filterBox.val(initialQuery);
    filter();
  }
});
