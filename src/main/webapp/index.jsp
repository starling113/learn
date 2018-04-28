<html>
<body>
<h2>Hello World!</h2>

<textarea id="msg" onkeyup="msgLength()" style="width: 300px; height: 100px;" >

</textarea>

<script>
    function msgLength() {
        console.log(document.getElementById("msg").value)
        console.log(document.getElementById("msg").value.length)
    }

</script>

</body>
</html>
