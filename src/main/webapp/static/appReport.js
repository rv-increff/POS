

function salesReport(){
let toDate = new Date(document.getElementById("toDate").value.trim());
let fromDate = new Date(document.getElementById("fromDate").value.trim());
let categoryInput = document.getElementById("categoryInputReports").value.trim();
let brandInput = document.getElementById("brandInputReports").value.trim();
console.log(toDate,fromDate,categoryInput,brandInput)

$.ajax({
     contentType: 'application/json',
     data: JSON.stringify({
           "to": toDate.toISOString(),
           "from": fromDate.toISOString(),
           "brand": brandInput,
           "category": categoryInput
          }),
     dataType: 'json',
     success: function(data){
         console.log(data);
         writeFileData(data,"Sales Report");
     },
     error: function(e){
     console.log(e)
         $("#confirmModalBody").html(`${e['responseJSON']['description']}`);
         $("#brandDelete").modal('show');
     },
     processData: false,
     type: 'POST',
     url: 'http://localhost:9000/pos/orders/sales-reports'
 });

}

function brandReport(){
console.log("brandReport")
$.ajax({
     contentType: 'application/json',
     dataType: 'json',
     success: function(data){
         console.log("brandReport generated",data);
         writeFileData(data,"Brand Report");
     },
     error: function(e){
     console.log(e)
          $("#confirmModalBody").html(`${e['responseJSON']['description']}`);
                   $("#brandDelete").modal('show');
     },
     processData: false,
     type: 'GET',
     url: 'http://localhost:9000/pos/brands/brand-reports'
 });

  event.preventDefault();
}


function inventoryReport(){
$.ajax({
     contentType: 'application/json',
     dataType: 'json',
     success: function(data){
         console.log(data);
         writeFileData(data,"Inventory Report");
     },
     error: function(e){
     console.log(e)
          $("#confirmModalBody").html(`${e['responseJSON']['description']}`);
                   $("#brandDelete").modal('show');
     },
     processData: false,
     type: 'GET',
     url: 'http://localhost:9000/pos/inventory/inventory-reports'
 });
  event.preventDefault();
}

function writeFileData(arr,fname){
console.log(fname," in write file data")
	var config = {
		quoteChar: '',
		escapeChar: '',
		delimiter: "\t"
	};

	var data = Papa.unparse(arr, config);
    var blob = new Blob([data], {type: 'text/tsv;charset=utf-8;'});
    var fileUrl =  null;

    if (navigator.msSaveBlob) {
        fileUrl = navigator.msSaveBlob(blob,  `${fname}.tsv`);
    } else {
        fileUrl = window.URL.createObjectURL(blob);
    }
    var tempLink = document.createElement('a');
    tempLink.href = fileUrl;
    tempLink.setAttribute('download', `${fname}.tsv`);
    tempLink.click();
}
