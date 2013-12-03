<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>

<body>
<form method="post" action="FileUploadServlet" enctype="multipart/form-data">
File: <input type="file" name="file" id="file" /> <br/>
<input type="submit" />
</form>
<a href = "Home2.jsp">Back</a>
</body>

</html>