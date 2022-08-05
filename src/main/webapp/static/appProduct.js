
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




function loadProduct(){
// Instantiate an new XHR Object
        const xhr = new XMLHttpRequest();

        // Open an obejct (GET/POST, PATH,
        // ASYN-TRUE/FALSE)
        xhr.open("GET","http://localhost:9000/pos/products", true);
        // When response is ready
        xhr.onload = function () {
            if (this.status === 200) {
                // Changing string data into JSON Object
                obj = JSON.parse(this.responseText);

                // Getting the ul element
                let body = document.getElementById("ProductTbody");
                str = ""
                for (var i=0;i<obj.length;i++) {
                    str += `<tr>
                                  <th scope="row">${i+1}</th>
                                  <td>${obj[i]['barcode']}</td>
                                  <td>${obj[i]['brand']}</td>
                                  <td>${obj[i]['category']}</td>
                                  <td>${obj[i]['name']}</td>
                                  <td>${obj[i]['mrp'].toFixed(2)}</td>
                                  <td><button type='button' class='btn btn-primary' onclick=editProductModal(${obj[i]['id']},"${obj[i]['barcode']}","${obj[i]['brand']}","${obj[i]['category']}","${obj[i]['name']}","${obj[i]['mrp']}")>Edit</button></td>
                                </tr>`;
                }
                body.innerHTML = str;
            }
            else {
                console.log("cannot fetch Product details");
            }
        }

        // At last send the request
        xhr.send();
}

function deleteProductModal(id){
document.getElementById("deleteModalBtn").setAttribute( "onClick", `deleteProduct(${id})` );
$("#confirmModalBody").html(`<p>Do you want to delete ?</p>`)
$("#ProductDelete").modal('show');
}
function deleteProduct(id){
  $.ajax({
                  type: "DELETE",
                  url: `http://localhost:9000/pos/products/${id}`,

                  success: function (data) {
                     loadProduct()
                     console.log(id, "is deleted")
                     $("#ProductDelete").modal('hide');
                  },
                  error: function (e) {
                    console.log(e,e['responseJSON']['description']);
                    $("#confirmModalBody").html(e['responseJSON']['description'])
                    document.getElementById("deleteModalBtn").setAttribute( "onClick", `` );
                    $("#ProductDelete").modal('show');
                  }
              });

                                }
function changeProduct(){
let Product = document.getElementById("BrandInput").value;
console.log("Product change",Product)
document.getElementById("BrandInput").setAttribute( "value", Product);
}
function changeCategory(){
let cat = document.getElementById("CategoryInput").value;
console.log("Product change",cat)
document.getElementById("CategoryInput").setAttribute( "value", cat);
}



function editProductModal(id,Barcode,Brand,Category,Name,MRP){
setEditModal();
console.log(id,Barcode,Brand,Category,Name,MRP)
$("#modalTitle").html("Update Product")
document.getElementById("BarcodeInput").setAttribute( "value", Barcode);
document.getElementById("BrandInput").setAttribute( "value", Brand );
document.getElementById("CategoryInput").setAttribute( "value", Category );
document.getElementById("NameInput").setAttribute( "value", Name );
document.getElementById("MRPInput").setAttribute( "value", MRP );
document.getElementById("EditModalBtn").setAttribute( "onclick", `updateProductForm(${id},'${Barcode}','${Brand}','${Category}','${Name}','${MRP}')` );
$("#EditModalBtn").text('Update');
$("#modalFormErrorDiv").html("");
$("#ProductEdit").modal('show');
}

function updateProductForm(id,Barcode,Brand,Category,Name,MRP){
let BarcodeInput = document.getElementById("BarcodeInput").value.trim();
let BrandInput = document.getElementById("BrandInput").value.trim();
let CategoryInput = document.getElementById("CategoryInput").value.trim();
let NameInput = document.getElementById("NameInput").value.trim();
let MRPInput = document.getElementById("MRPInput").value.trim();
console.log(MRPInput,BarcodeInput,BrandInput,CategoryInput,NameInput)

if(BarcodeInput==="" || BrandInput==="" || CategoryInput==="" || NameInput===""){
 $("#modalFormErrorDiv").html("*values cannot be empty and mrp must be a positive number ");
}
else if( MRPInput==="" || isNaN(MRPInput)){
  $("#modalFormErrorDiv").html("*MRP must be a positive number");
}
else if(BarcodeInput!==Barcode|| BrandInput!==Brand || CategoryInput!==Category || NameInput!==Name ||  MRPInput!==MRP){
 $.ajax({
     contentType: 'application/json',
     data: JSON.stringify({
           "barcode": BarcodeInput,
           "brand": BrandInput,
           "category": CategoryInput,
           "name": NameInput,
           "mrp": MRPInput,
           "id": id
     }),
     dataType: 'json',
     success: function(data){
         console.log("updated");
         $("#ProductEdit").modal('hide');
         loadProduct();
     },
     error: function(e){
     console.log(e)
         $("#modalFormErrorDiv").html(`*${e['responseJSON']['description']}`);
     },
     processData: false,
     type: 'PUT',
     url: 'http://localhost:9000/pos/products'
 });

}

 event.preventDefault();
}

function addProductModal(){
setEditModal();
$("#modalTitle").html("Add Product")
document.getElementById("BarcodeInput").setAttribute( "value", "");
document.getElementById("BrandInput").setAttribute( "value", "" );
document.getElementById("CategoryInput").setAttribute( "value", "" );
document.getElementById("NameInput").setAttribute( "value", "" );
document.getElementById("MRPInput").setAttribute( "value", "" );
document.getElementById("EditModalBtn").setAttribute( "onclick", `addProductForm()` );
$("#EditModalBtn").text('Add');
$("#modalFormErrorDiv").html("");
$("#ProductEdit").modal('show');

}

function addProductForm(){
let BarcodeInput = document.getElementById("BarcodeInput").value.trim();
let BrandInput = document.getElementById("BrandInput").value.trim();
let CategoryInput = document.getElementById("CategoryInput").value.trim();
let NameInput = document.getElementById("NameInput").value.trim();
let MRPInput = document.getElementById("MRPInput").value.trim();
console.log(BrandInput,CategoryInput,MRPInput)

if(BarcodeInput==="" || BrandInput==="" || CategoryInput==="" || NameInput==="" ){
 $("#modalFormErrorDiv").html("*values cannot be empty");
}
else if( MRPInput==="" || isNaN(MRPInput) ){
  $("#modalFormErrorDiv").html("*MRP must be a positive number");
}
else{
 $.ajax({
     contentType: 'application/json',
     data: JSON.stringify({

             "barcode": BarcodeInput,
             "brand": BrandInput,
             "category": CategoryInput,
             "name": NameInput,
             "mrp": MRPInput

     }),
     dataType: 'json',
     success: function(data){
         console.log("added");
         $("#ProductEdit").modal('hide');
         $("#modalFormErrorDiv").html("");
         loadProduct();
     },
     error: function(e){
     console.log(e)
         $("#modalFormErrorDiv").html(`*${e['responseJSON']['description']}`);
     },
     processData: false,
     type: 'POST',
     url: 'http://localhost:9000/pos/products'
 });
 event.preventDefault();
}}

function bulkAddProductModal(){
$("#uploadModalFormErrorDiv").html("");
$("#bulkUpload").modal('show');
}

function bulkAddProduct(){
let f = document.getElementById('formFile');
if(f.files && f.files[0]){
    console.log(f.files[0]['name'],f.files[0])
    var reader = new FileReader();
    reader.addEventListener('load', function (e) {
    let tsvData = e.target.result;
    let csvdata = e.target.result;
    bulkAddProductUtil(csvdata);

    });

   reader.readAsBinaryString(f.files[0]);

}
event.preventDefault();
}

function bulkAddProductUtil(data) {
        data = data.split('\r').join('');
        let parsedata = [];
        data = data.split('\r').join('');
        console.log(data)
        let newLinebrk = data.split("\n");
        console.log(newLinebrk);
        let head = newLinebrk[0].split("\t");
        console.log(head)
        let sortHead =[];
        Object.assign(sortHead,head);
        sorthead = sortHead.sort();
        console.log(sortHead,head)
        if(head.length!=5){
        $("#uploadModalFormErrorDiv").html("* Column names should be Barcode, Brand, Category, MRP and Name only");
        }
        else if(!(sortHead[0].toLowerCase() === "barcode" && sortHead[1].toLowerCase() === "brand" && sortHead[2].toLowerCase() === "category" && sortHead[3].toLowerCase() === "mrp" && sortHead[4].toLowerCase() === "name")){
        $("#uploadModalFormErrorDiv").html("* Column names should be Barcode, Brand, Category, MRP and Name only");
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
            if(row.length!=5){
                fileError += "syntax error in row " + i + "<br>";
                continue;
                }
            
            let jsonData = {};
            jsonData[head[0].toLowerCase()] = row[0].toLowerCase().trim();
            jsonData[head[1].toLowerCase()] = row[1].toLowerCase().trim();
            jsonData[head[2].toLowerCase()] = row[2].toLowerCase().trim();
            jsonData[head[3].toLowerCase()] = row[3].toLowerCase().trim();
            jsonData[head[4].toLowerCase()] = row[4].toLowerCase().trim();
            if(!parseFloat(jsonData['mrp'])){
              fileError += "*MRP must be a positive number error in row " + i + "<br>";
              continue
            }

            parsedata.push(jsonData);
                }
        console.table(parsedata);
        if(fileError!=""){
        console.log(fileError);
        $("#confirmModalBody").html(fileError);
        $("#bulkUpload").modal('hide');
        document.getElementById('ProductDelete').setAttribute("data-dismiss","");
        $('#ProductDelete').modal('show');

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
                       loadProduct();
                   },
                   error: function(e){
                   console.log(e)
                       console.log(fileError);
                               $("#confirmModalBody").html(e['responseJSON']['description'].replaceAll("\n","<br>"));
                               $("#bulkUpload").modal('hide');
                               document.getElementById('ProductDelete').setAttribute("data-dismiss","");
                               $('#ProductDelete').modal('show');
                   },
                   processData: false,
                   type: 'POST',
                   url: 'http://localhost:9000/pos/products/upload'
               });
        }
        }
    }


function setEditModal(){
$('#ProductEdit').html(`
               <div class="modal-dialog" role="document">
                 <div class="modal-content">
                   <div class="modal-header">
                     <h5 class="modal-title" id="modalTitle"></h5>
                     <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                       <span aria-hidden="true">&times;</span>
                     </button>
                   </div>
                   <div class="modal-body">
                     <form id="editProductForm" >
                       <div id="modalFormDataDiv">
                         <div class="form-group">
                           <label for="BarcodeInput">Barcode</label>
                           <input type="text" class="form-control" id="BarcodeInput" name="Barcode" aria-describedby="text" placeholder="Enter Barcode" autocomplete="off" minlength="1" onchange="changeProduct()" required>
                         </div>
                         <div class="form-group">
                           <label for="BrandInput">Brand</label>
                           <input type="text" class="form-control" id="BrandInput"  name="Brand" aria-describedby="text" placeholder="Enter Brand" autocomplete="off" minlength="1" onchange="changeCategory()">
                         </div>
                         <div class="form-group">
                           <label for="CategoryInput">Category</label>
                           <input type="text" class="form-control" id="CategoryInput" name="Category" aria-describedby="text" placeholder="Enter Category" autocomplete="off" minlength="1" onchange="changeProduct()">
                         </div>
                         <div class="form-group">
                           <label for="NameInput">Name</label>
                           <input type="text" class="form-control" id="NameInput"  name="Name" aria-describedby="text" placeholder="Enter Name" autocomplete="off" minlength="1" onchange="changeCategory()">
                         </div><div class="form-group">
                           <label for="MRPInput">MRP</label>
                           <input type="number" class="form-control" id="MRPInput"  name="MRP" aria-describedby="text" placeholder="Enter MRP" autocomplete="off" minlength="1" >
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