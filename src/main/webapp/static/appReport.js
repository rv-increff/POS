import {ZonedDateTime} from '@js-joda/root/packages/core/src/ZonedDateTime.js'

function salesReport(){
let toDate = ZonedDateTime.of2(document.getElementById("toDate").value.trim(),ZonedDateTime.zone());
let fromDate = ZonedDateTime.of2(document.getElementById("fromDate").value.trim(),ZonedDateTime.zone());
let categoryInput = document.getElementById("categoryInputReports").value.trim();
let brandInput = document.getElementById("brandInputReports").value.trim();
console.log(toDate,fromDate,categoryInput,brandInput)

$.ajax({
     contentType: 'application/json',
     data: JSON.stringify({
           "to": toDate,
           "from": fromDate,
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
     url: 'http://localhost:9000/pos/api/invoices/get-sales'
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
     url: 'http://localhost:9000/pos/api/invoices/get-brand-report'
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
     url: 'http://localhost:9000/pos/api/invoices/get-inventory-report'
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
