function createOrder() {
  $.ajax({
    contentType: 'application/json',

    success: function (data) {
      console.log("inserted");
      document.getElementById('cancelBtnModal').style.visibility = "hidden";
      document.getElementById('deleteModalBtn').setAttribute("onClick", "createOrderUtil()");
      $('#confirmModalBody').html('<p>New order created, you can now add products in it</p>')

      $("#alertModal").modal('show');
      loadOrder();
    },
    error: function (e) {
      console.log(e)
      console.log(fileError);
      $("#confirmModalBody").html(e['responseJSON']['description']);
      $("#bulkUpload").modal('hide');
      document.getElementById('ProductDelete').setAttribute("data-dismiss", "");
      $('#ProductDelete').modal('show');
    },
    processData: false,
    type: 'POST',
    url: 'http://localhost:9000/pos/orders'
  });
}

function createOrderUtil() {
  $("#alertModal").modal('hide');
  document.getElementById('cancelBtnModal').style.visibility = "";
}

function loadOrder() {

  $.ajax({
    contentType: 'application/json',

    success: function (data) {
      let body = document.getElementById("OrderTbody");
      let str = ""
      for (var i = 0; i < data.length; i++) {
        let t = data[i]
        time = t['time'].split('T').join(" ")
        console.log(t,time)
        if (t['orderPlaced']) {
          str += `
                            <tr>
                                          <th scope="row">${i + 1}</th>
                                          <td>${time}</td>
                                          <td><button type='button' class='btn btn-primary' onclick=ViewOrder(${t['id']},1) id ="editBtn${t['id']}">View</button></td>
                                          <td><button type='button' class='btn btn-primary' onclick=invoice(${t['id']}) id ="invoiceBtn${t['id']}" style="padding-right: 29px;padding-left: 29px;">Invoice</button></td>
                            </tr>
                                       `;
        }
        else {
          str += `
                            <tr>
                                          <th scope="row">${i + 1}</th>
                                          <td>${time}</td>
                                          <td><button type='button' class='btn btn-primary' onclick=ViewOrder(${t['id']},0) id ="editBtn${t['id']}">View</button></td>
                                          <td><button type='button' class='btn btn-success' onclick=placeOrder(${t['id']}) id ="invoiceBtn${t['id']}">Place Order</button></td>
                            </tr>
                                       `;
        }

      }

      body.innerHTML = str;

    },
    error: function (e) {
      console.log(e)
    },
    processData: false,
    type: 'GET',
    url: 'http://localhost:9000/pos/orders'
  });
}
function invoice(orderId) {
  $.ajax({
    contentType: 'application/json', xhr: function () {
      const xhr = new XMLHttpRequest();
      xhr.responseType = 'blob'
      return xhr;
    },
    success: function (data) {
      // data.append("pdf", blob, "invoice.pdf");
      console.log(data)
      var file = new Blob([data], { type: 'application/octet-stream' },);
      file.name = "invoice.pdf"
      var fileURL = URL.createObjectURL(file);
      setTimeout(() => {
        window.open(fileURL,'_blank');
      })
      console.log(fileURL);
    },
    error: function (e) {
      console.log(e)
      return ""

    },
    processData: false,
    type: 'GET',
    url: `http://localhost:9000/pos/orders/${orderId}/invoices`
  });
}
function placeOrder(id) {
  $.ajax({
    contentType: 'application/json',
    dataType: 'json',
    success: function (data) {
      if(data.length>0){
      $("#alertModal").modal('show');
      $('#confirmModalBody').html('<p>Confirm place order</p>')
      document.getElementById('deleteModalBtn').setAttribute("onClick", `placeOrderUtil(${id})`);}
      else{
        $("#alertModal").modal('show');
      $('#confirmModalBody').html('<p>Order Empty! add Items in order first</p>')
      document.getElementById('deleteModalBtn').setAttribute("onClick", `hideModal()`);}
    },
    error: function (e) {
      console.log(e)
      $("#modalFormErrorDiv").html(`*${e['responseJSON']['description']}`);
    },
    processData: false,
    type: 'GET',
    url: `http://localhost:9000/pos/orders/${id}/order-items`
  });
  
}

function placeOrderUtil(id) {

  $.ajax({
    contentType: 'application/json',
    dataType: 'json',
    success: function (data) {
      console.log("order placed orderId", id);
      $("#alertModal").modal('hide');
      loadOrder();
    },
    error: function (e) {
      console.log(e)
      $("#modalFormErrorDiv").html(`*${e['responseJSON']['description']}`);
    },
    processData: false,
    type: 'PUT',
    url: `http://localhost:9000/pos//orders/${id}/place-order`
  });
}

function ViewOrder(orderId, orderStatus) {
  $.ajax({
    contentType: 'application/json',
    success: function (data) {
      console.log(data);
      let obj = data;
      let totalQty = 0;
      let totalCost = 0;
      let body = document.getElementById("orderItemModal-body");
      console.log(body)
      let str = ""
      if (orderStatus) {
        str = `<div class="jumbotron" id="orderItemView">
                          <table class="table">
                            <thead class="thead-dark">
                            <tr>
                              <th scope="col">#</th>
                              <th scope="col">ProductId</th>
                              <th scope="col">Quantity</th>
                              <th scope="col">Selling Price</th>
                           
                            </tr>
                            </thead>
                            <tbody id="">
                                </div>`
        for (var i = 0; i < obj.length; i++) {
          totalCost += obj[i]['quantity'] * obj[i]['sellingPrice'];
          totalQty += obj[i]['quantity'];
          str += `
                      <tr><th scope="row">${i + 1}</th>
                                   
                                    <td>${obj[i]['productId']}</td>
                                    <td>${obj[i]['quantity']}</td>
                                    <td>${obj[i]['sellingPrice']}</td>
                                  </tr>`;
        }

        str += `<td>Total Qunatity : ${totalQty}
        </td> <td>Total Cost : ${totalCost.toFixed(2)}</td>
         </tbody></table>`
      }
      else {


        str = `<div class="jumbotron" id="orderItemView">
                                <table class="table">
                                  <thead class="thead-dark">
                                  <tr>
                                    <th scope="col">#</th>
                                    <th scope="col">ProductId</th>
                                    <th scope="col">Quantity</th>
                                    <th scope="col">Selling Price</th>
                                    <th scope="col">Edit</th>
                                    <th scope="col">Delete</th>
                                  </tr>
                                  </thead>
                                  <tbody id="">
                                      </div>`
        for (var i = 0; i < obj.length; i++) {
          totalCost += obj[i]['quantity'] * obj[i]['sellingPrice'];
          totalQty += obj[i]['quantity'];
          str += `
                            <tr><th scope="row">${i + 1}</th>
                                          <td>${obj[i]['productId']}</td>
                                          <td>${obj[i]['quantity']}</td>
                                          <td>${obj[i]['sellingPrice']}</td>
                                          <td><button type='button' class='btn btn-primary' onclick=editOrderItem(${obj[i]['id']},${obj[i]['orderId']},${obj[i]['productId']},${obj[i]['quantity']},${obj[i]['sellingPrice']})>Edit</button></td>
                                          <td><button type='button' class='btn btn-primary' onclick=deleteOrderItem(${obj[i]['id']},${obj[i]['orderId']})>Delete</button></td>
                                        </tr>`;
        }

        str += ` <td>Total Qunatity : ${totalQty}
        </td> <td>Total Cost : ${totalCost.toFixed(2)}</td> </tbody></table> <td><button type='button' class='btn btn-primary' onclick=addOrderItem(${orderId})>Add</button></td>`
      }
      body.innerHTML = str;
      $('#orderItemModal').modal('show');
    },
    error: function (e) {
      console.log(e)

    },
    processData: false,
    type: 'GET',
    url: `http://localhost:9000/pos/orders/${orderId}/order-items`
  });


}

function editOrderItem(id, orderId, productId, quantity, sellingPrice) {
  $('#orderItemModal').modal('hide');
  let divi = document.getElementById('modalFormDataDiv')
  let str = `
            <div class="form-group" >
              <label for="productInput">Order Id</label><br>
              <label class="btn btn-secondary" for="productInput" style="width:75.8px;padding:.375rem .75rem;">${orderId}</label>

            </div>
            <div class="form-group" >
              <label for="productInput">Product Id</label><br>
              <label class="btn btn-secondary" for="productInput" style="width:75.8px;padding:.375rem .75rem;">${productId}</label>

            </div>
           <div class="form-group" >
         <label for="quantityInput">Quantity</label>
         <input type="number" class="form-control" id="quantityInput" value=${quantity} name="category" aria-describedby="text" placeholder="Enter Quantity" autocomplete="off" minlength="1" maxlength="9" oninput="javascript: if (this.value.length > this.maxLength) this.value = this.value.slice(0, this.maxLength);">
       </div>
       <div class="form-group">
         <label for="sellingPriceInput">Selling Price</label>
         <input type="number" class="form-control" id="sellingPriceInput" value=${sellingPrice} name="category" aria-describedby="text" placeholder="Enter Selling Price" autocomplete="off" minlength="1" maxlength="9" oninput="javascript: if (this.value.length > this.maxLength) this.value = this.value.slice(0, this.maxLength);">
       </div>
            <div id="modalFormErrorDiv" style="color:red; font-size: 14px;">
            <!--error-->
            </div>`

  divi.innerHTML = str;
  document.getElementById('EditModalBtn').innerHTML = "Update"
  document.getElementById('EditModalBtn').setAttribute("onClick", `editOrderItemUtil(${id},${quantity},${sellingPrice},${orderId})`);
  $("#OrderItemEdit").modal('show');
}

function deleteOrderItem(id, orderId) {
  $('#confirmModalBody').html('<p>Do you want to delete this item in your order?</p>')
  document.getElementById('deleteModalBtn').setAttribute("onClick", `deleteOrderItemUtil(${id},${orderId})`);
  $('#orderItemModal').modal('hide');
  $('#alertModal').modal('show')

}

function editOrderItemUtil(id, quantity, sellingPrice, orderId) {
  quantityInput = document.getElementById('quantityInput').value.trim()
  sellingPriceInput = document.getElementById('sellingPriceInput').value.trim()
  console.log(parseInt(quantityInput), sellingPriceInput, quantity, sellingPrice)

  let p2 = /^[0-9]+$|^[0-9]+\.[0]*$/
  let p3 = /^[0-9]+$|^[0-9]+\.[0-9]*$/
  if (!quantityInput.match(p2) || !sellingPriceInput.match(p3)) {
    document.getElementById('modalFormErrorDiv').innerHTML = "* Invalid input"
  }
  else if (parseFloat(quantity) !== parseFloat(quantityInput) || parseFloat(sellingPrice) != parseFloat(sellingPriceInput)) {
    $.ajax({
      contentType: 'application/json',
      data: JSON.stringify({
        "id": id,
        "quantity": parseInt(quantityInput),
        "sellingPrice": sellingPriceInput
      }),
      dataType: 'json',
      success: function (data) {
        console.log("updated orderItem");
        $("#OrderItemEdit").modal('hide');
        //location.reload();
        ViewOrder(orderId, 0);


      },
      error: function (e) {
        console.log(e)
        $("#modalFormErrorDiv").html(`*${e['responseJSON']['description']}`);
      },
      processData: false,
      type: 'PUT',
      url: `http://localhost:9000/pos/order-items/${id}`
    });
  }
}


function hideModal() {
  $('#alertModal').modal('hide')
}


function deleteOrderItemUtil(id, orderId) {
  $.ajax({
    contentType: 'application/json',
    success: function (data) {
      $('#alertModal').modal('hide')
      ViewOrder(orderId, 0)
    },
    error: function (e) {
      $("#confirmModalBody").html(`<p>Error:</p><p>*${e['responseJSON']['description']}</p>`);
      document.getElementById('deleteModalBtn').setAttribute("onClick", `hideModal()`);
      console.log(e)
    },
    processData: false,
    type: 'DELETE',
    url: `http://localhost:9000/pos/order-items/${id}`
  });
}

function addOrderItem(orderId) {
  $('#orderItemModal').modal('hide');
  document.getElementById('EditModalBtn').setAttribute("onClick", `addOrderItemUtil(${orderId})`)
  document.getElementById('EditModalBtn').innerHTML = "Add"
  document.getElementById('modalFormErrorDiv').innerHTML = ""
  let divi = document.getElementById('modalFormDataDiv')
  let str = `
            <div class="form-group" >
              <label for="productInput">Product Id</label>
              <input type="number" class="form-control" id="productInput"  name="category" aria-describedby="text" placeholder="Enter Product Id" autocomplete="off" minlength="1">
            </div>
            <div class="form-group" >
              <label for="quantityInput">Quantity</label>
              <input type="number" class="form-control" id="quantityInput"  name="category" aria-describedby="text" placeholder="Enter Quantity" autocomplete="off" minlength="1" oninput="javascript: if (this.value.length > this.maxLength) this.value = this.value.slice(0, this.maxLength);" maxlength="9">
            </div>
            <div class="form-group">
              <label for="sellingPriceInput">Selling Price</label>
              <input type="number" class="form-control" id="sellingPriceInput"  name="category" aria-describedby="text" placeholder="Enter Selling Price" autocomplete="off" minlength="1" >
            </div>
            <div id="modalFormErrorDiv" style="color:red; font-size: 14px;">
            <!--error-->
            </div>`

  divi.innerHTML = str;
  $("#OrderItemEdit").modal('show');
}

function addOrderItemUtil(orderId) {

  productInput = document.getElementById('productInput').value.trim()
  quantityInput = document.getElementById('quantityInput').value.trim()
  sellingPriceInput = document.getElementById('sellingPriceInput').value.trim()
  console.log(productInput, parseInt(quantityInput), sellingPriceInput)
  let p1 = /^[0-9]+$/
  let p2 = /^[0-9]+$|^[0-9]+\.[0]*$/
  let p3 = /^[0-9]+$|^[0-9]+\.[0-9]*$/
  if (!productInput.match(p1) || !quantityInput.match(p2) || !sellingPriceInput.match(p3)) {
    document.getElementById('modalFormErrorDiv').innerHTML = "* Invalid input"
  }
  else {
    $.ajax({
      contentType: 'application/json',
      data: JSON.stringify({
        "orderId": orderId,
        "productId": parseInt(productInput),
        "quantity": parseInt(quantityInput),
        "sellingPrice": sellingPriceInput
      }),
      dataType: 'json',
      success: function (data) {
        console.log("inserted orderItem");
        $("#OrderItemEdit").modal('hide');
        //location.reload();
        //        loadOrder()
        ViewOrder(orderId, 0);

      },
      error: function (e) {
        console.log(e)
        $("#modalFormErrorDiv").html(`*${e['responseJSON']['description']}`);
      },
      processData: false,
      type: 'POST',
      url: 'http://localhost:9000/pos/order-items'
    });

  }
}

function getTable(orderId) {



}