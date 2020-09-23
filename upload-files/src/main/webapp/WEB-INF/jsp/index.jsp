<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Upload File</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
</head>


<body>

<form id="form1">

    <input type="file" name="file">

</form>

<button type="button" onclick="submit();">Upload</button>

<br/>
<br/>

<table>
<thead>
    <th>Name</th>
    <th>Size</th>
    <th>Version</th>
</thead>

<tbody id="tbody">
    <c:forEach var="j" items="${files}" varStatus="loop">
       <tr>
       <td>${j.name}</td>
       <td>${j.size}</td>
       <td>
       <select id="selectedVersion${loop.index}">
       <option value="0">Select</option>
           <c:forEach var="v" items="${j.versions}">
                <option>${v}</option>
           </c:forEach>
       </select>
       </td>
       <td><input type="button" value="Edit" onclick="getFileContentByName('${j.name}', '${loop.index}');"></td>
       </tr>
    </c:forEach>
</tbody>
</table>


<div class="container">
  <!-- Modal -->
  <div class="modal fade" id="myModal" role="dialog">
    <div class="modal-dialog">

      <!-- Modal content-->
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal">&times;</button>
          <h4 class="modal-title" id="modelTitle"></h4>
        </div>
        <div class="modal-body">
          <textarea rows="10" cols="50" id="body"></textarea>
          <hidden id="name"></hidden>
          <hidden id="version"></hidden>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
          <button type="button" class="btn btn-default" onclick="update();">Update</button>
          <button type="button" class="btn btn-default" onclick="restoreVersion();">Restore Version</button>
        </div>
      </div>

    </div>
  </div>

</div>


<script>

function restoreVersion(){

    var version = $("#version").val();
    var name = $("#name").val();

    if(confirm("Are you sure, you want to restore to "+version+" ?")){
        $.ajax({
                url: "file/restoreversion/"+name+"/"+version,
                type: "put",
                success: function(resp){
                    alert(resp);
                    getFiles();
                },
                error: function(xhr, status, error){
                    alert(xhr.responseText);
                    getFiles();
                }
        });
    }
}


function update(){

    var value = $("#body").val();
    var name = $("#name").val();
    var values = {dataValue : value};

    $.ajax({
        url: "file/updatefiledata/"+name,
        type: "put",
        contentType: 'application/json',
        data: JSON.stringify(values),
        dataType: 'json',
        success: function(resp){
            alert(resp);
            getFiles();
            $('#modal').modal('toggle');
        },
        error: function(xhr, status, error){
            alert(xhr.responseText);
            getFiles();
        }
    });
}

function getFileContentByName(name, index){

    var version = $("#selectedVersion"+index).val();

    $.ajax({
        url: "file/"+name+"/"+version,
        type: "get",
        method: "get",
        cache: false,
        success: function(resp){
            openModal();
            $("#modelTitle").html(name+" V "+version);
            $("#body").html(resp);
            $("#name").val(name);
            $("#version").val(version);

        },
        error: function(xhr, status, err){
            alert(xhr.responseText);
        }
    });
}

function getFiles(){

    $.ajax({
        url: "file/files",
        method: "get",
        type: "get",
        cache: false,
        success: function(resp){
            var response = "";

            for(var i =0;i<resp.length;i++){
                response += "<tr> <td>"+ resp[i].name +"</td> <td>"+ resp[i].size +"</td> <td> <select id='selectedVersion'"+ i +"><option value='0'>Select</option>"

                for(var j=0;j<resp[i].versions.length;j++){
                    response+= "<option>"+ resp[i].versions[j] +"</option>"
                }

                response+= "</select></td> <td><button type='button' onclick='getFileContentByName('"+ resp[i].name +"', '"+ i +"');'>Edit</button></td> </tr>"
            }

            $("#tbody").html(response);

        },
        error: function(xhr, status, err){
            alert(xhr.responseText);
        }
    });
}

function submit(){
    var formData = new FormData($("#form1")[0]);
    $.ajax({
        url : "file/upload",
        type : 'post',
        data : formData,
        enctype : "multipart/form-data",
        contentType : false,
        cache : false,
        processData : false,
        success : function(resp) {
            alert(resp);
            //getFiles();
            document.location.reload();
        },
        error: function(xhr, status, err){
            alert(xhr.responseText);
            //getFiles();
        }
        });
}

function openModal(){
    $("#myModal").modal();
}

</script>

</body>
</html>