
//HELPER METHOD
function toJson($form){
    var serialized = $form.serializeArray();
    console.log(serialized);
    var s = '';
    var data = {};
    for(s in serialized){
        data[serialized[s]['name']] = serialized[s]['value']
    }
    var json = JSON.stringify(data);
    return json;
}


function handleAjaxError(response){
	var response = JSON.parse(response.responseText);
	alert(response.message);
}

function readFileData(file, callback){
	var config = {
		header: true,
		delimiter: "\t",
		skipEmptyLines: "greedy",
		complete: function(results) {
			callback(results);
	  	}	
	}
	Papa.parse(file, config);
}


function loadInventory(){
// Instantiate an new XHR Object
        const xhr = new XMLHttpRequest();

        // Open an obejct (GET/POST, PATH,
        // ASYN-TRUE/FALSE)
        xhr.open("GET","http://localhost:9000/pos/inventory", true);
        // When response is ready
        xhr.onload = function () {
            if (this.status === 200) {
                // Changing string data into JSON Object
                obj = JSON.parse(this.responseText);

                // Getting the ul element
                let body = document.getElementById("inventoryTbody");
                str = ""
                for (var i=0;i<obj.length;i++) {
                    str += `<tr>
                                  <th scope="row">${i+1}</th>
                                  <td>${obj[i]['barcode']}</td>
                                  <td>${obj[i]['quantity']}</td>
                                  <td><button type='button' class='btn btn-primary' onclick=editInventoryModal(${obj[i]['id']},"${obj[i]['barcode']}","${obj[i]['quantity']}")>Edit</button></td>
                                </tr>`;
                }
                body.innerHTML = str;
            }
            else {
                console.log("cannot fetch inventory details");
            }
        }

        // At last send the request
        xhr.send();
}

function deleteInventoryModal(id){
document.getElementById("deleteModalBtn").setAttribute( "onClick", `deleteInventory(${id})` );
$("#confirmModalBody").html(`<p>Do you want to delete ?</p>`)
$("#inventoryDelete").modal('show');
}
function deleteInventory(id){
  const xhr = new XMLHttpRequest();
//          xhr.open("DELETE",`http://localhost:9000/pos/inventory/delete/${id}`, true);
  $.ajax({
                  type: "DELETE",
                  url: `http://localhost:9000/pos/inventory/delete/${id}`,

                  success: function (result, status, xhr) {
                     loadInventory()
                     console.log(id, "is deleted")
                  },
                  error: function (e) {
                    console.log(e,e['responseJSON']['description']);
                    $("#confirmModalBody").html(e['responseJSON']['description'])
                    document.getElementById("deleteModalBtn").setAttribute( "onClick", `` );
                    $("#inventoryDelete").modal('show');
                  }
              });

$("#inventoryDelete").modal('hide');
                                }


function editInventoryModal(id,barcode,quantity){
setEditModal()
console.log(id,barcode,quantity)
$("#modalTitle").html("Update Inventory")
document.getElementById("quantityInput").setAttribute( "value", quantity);
document.getElementById("EditModalBtn").setAttribute( "onclick", `updateInventoryForm(${id},'${barcode}','${quantity}')` );
$("#EditModalBtn").text('Update');
$("#editDiv").html(`<label for="barcodeInput">Barcode</label><br>
                    <label class="btn btn-secondary" style="padding:.375rem .75rem;">${barcode}</label>`)
$("#modalFormErrorDiv").html("");
$("#inventoryEdit").modal('show');
}

function updateInventoryForm(id,barcode,quantity){
let quantityInput = document.getElementById("quantityInput").value.trim();
console.log(barcode,quantity,quantityInput)
console.log(id,barcode,quantity)
if(!(quantityInput.match(/^[0-9]+$/) || quantityInput.match(/^[0-9]+.[0]*$/))){
 $("#modalFormErrorDiv").html("*quantity must be a positive whole number");

}
else if(parseInt(quantityInput)==0){
 $("#modalFormErrorDiv").html("*quantity must be a positive whole number");
}
else if(parseInt(quantityInput)!==parseInt(quantity)){
 $.ajax({
     contentType: 'application/json',
     data: JSON.stringify({
           "quantity": parseInt(quantityInput),
           "id": id

     }),
     dataType: 'json',
     success: function(data){
         console.log("updated");
         $("#inventoryEdit").modal('hide');
         loadInventory();
     },
     error: function(e){
     console.log(e)
         $("#modalFormErrorDiv").html(`*${e['responseJSON']['description']}`);
     },
     processData: false,
     type: 'PUT',
     url: `http://localhost:9000/pos/inventory/${id}`
 });

}

 event.preventDefault();
}

function addInventoryModal(){
setEditModal();
$("#modalTitle").html("Add Inventory")
document.getElementById("barcodeInput").setAttribute( "value", "");
document.getElementById("quantityInput").setAttribute( "value", "" );
document.getElementById("EditModalBtn").setAttribute( "onclick", `addInventoryForm()` );
$("#editDiv").html(
`<label for="barcodeInput">Barcode</label>
<input type="text" class="form-control" id="barcodeInput" name="barcodeInput" aria-describedby="text" placeholder="Enter Barcode" autocomplete="off" minlength="1">`)

$("#EditModalBtn").text('Add');
$("#modalFormErrorDiv").html("");
$("#inventoryEdit").modal('show');


}

function addInventoryForm(){
let barcodeInput = document.getElementById("barcodeInput").value.trim();
let quantityInput = document.getElementById("quantityInput").value.trim();
console.log(barcodeInput,quantityInput)
let p2 = /^[0-9]+$|^[0-9]+\.[0]*$/
let p3 = /^[0-9]+$|^[0-9]+\.[0-9]*$/

if(barcodeInput=="" ){
 $("#modalFormErrorDiv").html("*barcode cannot be empty");
}
else if(quantityInput=="" || !quantityInput.match(p2)){
 $("#modalFormErrorDiv").html("*quantity must be a positive whole number");

}
else if(parseInt(quantityInput)==0){
 $("#modalFormErrorDiv").html("*quantity must be a positive whole number");
}
else{
 $.ajax({
     contentType: 'application/json',
     data: JSON.stringify({

           "barcode": barcodeInput,
           "quantity": parseInt(quantityInput)
     }),
     dataType: 'json',
     success: function(data){
         console.log("updated");
         $("#inventoryEdit").modal('hide');
         $("#modalFormErrorDiv").html("");
         loadInventory();
     },
     error: function(e){
     console.log(e)
         $("#modalFormErrorDiv").html(`*${e['responseJSON']['description']}`);
     },
     processData: false,
     type: 'POST',
     url: 'http://localhost:9000/pos/inventory'
 });
 event.preventDefault();
}}

function bulkAddInventoryModal(){
$("#uploadModalFormErrorDiv").html("");
$("#bulkUpload").modal('show');
}

function bulkAddInventory(){
let f = document.getElementById('formFile');
if(f.files && f.files[0]){
    console.log(f.files[0]['name'],f.files[0])
    var reader = new FileReader();
    reader.addEventListener('load', function (e) {
    let tsvData = e.target.result;
    let csvdata = e.target.result;
    bulkAddInventryUtil(csvdata);

    });

   reader.readAsBinaryString(f.files[0]);

}
event.preventDefault();
}

function bulkAddInventryUtil(data) {
        data = data.split('\r').join('');
        let parsedata = [];
        let p2 = /^[0-9]+$|^[0-9]+\.[0]*$/
        let newLinebrk = data.split("\n");
        console.log(newLinebrk);
        head = newLinebrk[0].split("\t");
        console.log(head)
        if(head.length!=2){
        $("#uploadModalFormErrorDiv").html("* Column names should be barcode and quantity only");
        }
        else if(!((head[0].toLowerCase() === "barcode" && head[1].toLowerCase() === 'quantity') || (head[1].toLowerCase() === "barcode" && head[0].toLowerCase() === 'quantity'))){
        $("#uploadModalFormErrorDiv").html("* Column names should be barcode and quantity only");
        }
        else if(newLinebrk.length>1001){
        $("#uploadModalFormErrorDiv").html("* Please upload a file with less that 1000 rows<br>LIMIT IS 1000 ROWS");
        }
        else{
        fileError = ""
        for(let i = 1; i < newLinebrk.length; i++) {
            row = newLinebrk[i].split("\t");
            console.log(row);
            if(row.length==1 && row[0]==""){
            continue;
            }
            if(row.length!=2){
                fileError += `Error row ${i} -> syntax error <br>`;
                continue;
                }
            let jsonData = {};
            jsonData[head[0].toLowerCase()] = row[0].toLowerCase();
            jsonData[head[1].toLowerCase()] = row[1].toLowerCase();
            if(!jsonData['quantity'].match(p2)){
              fileError += `Error row ${i} -> quantity must be a whole number <br>`;
            }
            jsonData['quantity'] = parseInt(jsonData['quantity']);
            parsedata.push(jsonData);
                }
        console.table(parsedata);
        if(fileError!=""){
        console.log(fileError);
        $("#confirmModalBody").html(fileError);
        $("#bulkUpload").modal('hide');
        document.getElementById('inventoryDelete').setAttribute("data-dismiss","");

        $('#inventoryDelete').modal('show');

        }
        else{
        const xhr = new XMLHttpRequest();
      $.ajax({ contentType: 'application/json',
                   data: JSON.stringify(parsedata),
                   dataType: 'json',
                   success: function(data){
                       console.log("inserted");
                       $("#bulkUpload").modal('hide');
                       $("#modalFormErrorDiv").html("");
                       loadInventory();
                   },
                   error: function(e){
                   console.log(e)
                       console.log(fileError);
                               $("#confirmModalBody").html(e['responseJSON']['description'].replaceAll("\n","<br>"));
                               $("#bulkUpload").modal('hide');
                               document.getElementById('inventoryDelete').setAttribute("data-dismiss","");

                               $('#inventoryDelete').modal('show');
                   },
                   processData: false,
                   type: 'POST',
                   url: 'http://localhost:9000/pos/inventory/upload'
               });
        }



        }

    }


function setEditModal(){
console.log("edit modeal set");

$('#inventoryEdit').html(`
                            <div class="modal-dialog" role="document">
                              <div class="modal-content">
                                <div class="modal-header">
                                  <h5 class="modal-title" id="modalTitle"></h5>
                                  <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                  </button>
                                </div>
                                <div class="modal-body">
                                  <form id="editInventoryForm" >
                                    <div id="modalFormDataDiv">
                                      <div class="form-group" id="editDiv">
                                        <label for="barcodeInput">Barcode</label>
                                        <input type="text" class="form-control" id="barcodeInput" name="barcodeInput" aria-describedby="text" placeholder="Enter Barcode" autocomplete="off" minLength="1" maxlength="9">
                                      </div>
                                      <div class="form-group">
                                        <label for="quantityInput">Quantity</label>
                                        <input type="number" class="form-control" id="quantityInput"  name="quantityInput" aria-describedby="text" placeholder="Enter Quantity" autocomplete="off" minLength="1" oninput="javascript: if (this.value.length > this.maxLength) this.value = this.value.slice(0, this.maxLength);" maxlength="9" >
                                      </div>
                                      <div id="modalFormErrorDiv" style="color:red; font-size: 14px;">
                                      <!--error-->
                                      </div>
                                    </div>
                                    <div class="modal-footer">
                                      <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                                      <button type="button" class="btn btn-primary" id="EditModalBtn" ></button>
                                    </div>
                                  </form>
                                </div>

                              </div>
                            </div>
                          `)

}