//var table = document.getElementsByTagName("table")[0];

//clear table then fill it, uses results from get requests to hygiene.php
function fillTable(obj){
  $("table").children().remove();
  obj.forEach((bus) => {
    $("table").append(
      "<tr><td>" + bus.name + "</td><td>" + bus.address + "</td><td>" + bus.type+ "</td><td>" + bus.rating + "</td><td>" + bus.date + "</td><td>" + "<button> get rating </button>" + "</td></tr>");
    $("table").children().last().find("button").click(() => {
      getRating(bus.name, bus.address);
    });
  })
}

//get for business name, fill table with results
function search(businessName){
  $.get("https://x.com/hygiene.php", {"op": "search", "name": businessName}, function(res){
    fillTable(JSON.parse(res));
    console.log(res);
  })
}

//get num of pages, fill table with results
function getPages(pageNum){
  $.get("https://x.com/hygiene.php", {"op": "get", "page": pageNum}, function(res){
    fillTable(JSON.parse(res));
  })
}

function getRating(business, address){
  $.get("https://x.com/rating.php", {"business": business}, function(res){
    //let obj = JSON.parse(res[0]);
    if (res.length>1){
      res.filter((elem)=>{elem.address == address})
    }
    if (res.length == 0){
      alert(("no results found for: ") + business);
    } else {
      let toShow = "rating for " + business + ": average rating: " + res[0].rating + ", number of ratings: " + res[0].total;
      alert(toShow);
    }
  })
}

$(document).ready(function(){
  //setup initial table
  getPages("1");  
  //get num pages
  $.get("https://x.com/hygiene.php", {"op": "pages"}, function(res){
    let numPages = JSON.parse(res).pages;
    //add buttons for pages with get requests linked to each page
    for (let i=0; i<numPages; i++){
      $("#buttons").append("<button>"+(i+1)+"</button>");
      $("#buttons").children().eq(i).click(()=>{
        $("table").children().remove();
        getPages((i+1))
      })
    }
  })

  //setup search button
  $("#searchBtn").click(function() {
      const businessName = $('#searchInp').val();
      search(businessName);
  });
});
