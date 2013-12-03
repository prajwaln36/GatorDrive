<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" href="Style1.css" type="text/css">
<title>Gator Drive : A Distributed File Storage System</title>

<script>
function inputFocus(i){
    if(i.value==i.defaultValue){ i.value=""; i.style.color="#000"; }
}
function inputBlur(i){
    if(i.value==""){ i.value=i.defaultValue; i.style.color="#888"; }
}
</script>

</head>

<body>

<div id="maincontainer">

<div id="topsection"><div class="innertube">

<div id="header_leftpane" align="center">
<table>
<tr>
<td> <img src="IndexPageTitle.png"> </td>
</tr>
<tr>
<td> <h1> Gator Drive: A Distributed File Storage System </h1> </td>
</tr>
</table>
</div>

<div id="header_rightpane"  align="center">
	<form name="LoginForm" action="Controller" onsubmit="return validateForm();" method="get">
		<table>
			<tr>
				<td>
					<input type="text" name="username" title="Username" style="color:#888;" value="Username" onfocus="inputFocus(this)" onblur="inputBlur(this)" />
				</td>
				<td>
					<input type="text" name="password" title="Password" style="color:#888;" value="Password" onfocus="inputFocus(this)" onblur="inputBlur(this)" />
				</td>
				<td>
					<input type="submit" name="login" value="Login" />
				</td>				
			</tr>
		</table>
	</form>
</div>

</div>
</div>

<div id="contentwrapper"  align="center">
<div id="leftcolumn"> <div class="innertube"> <img src="IndexPageGatorLogo.jpg"> </div> </div>

<div id="contentcolumn">
<div class="innertube">
<form name="RegisterForm" action="#" onsubmit="return validateRegisterForm();" method="post">
		<p > <b> Sign Up </b> </p>
		
		<table>
			<tr>
				<td>
					<input type="text" name="firstname" title="First Name" style="color:#888;" value="First Name" onfocus="inputFocus(this)" onblur="inputBlur(this)" />
				</td>
				<td>
					<input type="text" name="lastname" title="Last Name" style="color:#888;" value="Last Name" onfocus="inputFocus(this)" onblur="inputBlur(this)" />
				</td>				
			</tr>
			<tr>
				<td>
					<input type="text" name="email" title="Email" style="color:#888;" value="Email" onfocus="inputFocus(this)" onblur="inputBlur(this)" />
				</td>				
			</tr>
			<tr>
				<td>
					<input type="text" name="username" title="username" style="color:#888;" value="Username" onfocus="inputFocus(this)" onblur="inputBlur(this)" />
				</td>				
			</tr>
			<tr>
				<td>
					<input type="text" name="password" title="Password" style="color:#888;" value="Password" onfocus="inputFocus(this)" onblur="inputBlur(this)" />
				</td>				
			</tr>
			<tr>
				<td>
					<input type="text" name="confirmpassword" title="Confirm Password" style="color:#888;" value="Confirm Password" onfocus="inputFocus(this)" onblur="inputBlur(this)" />
				</td>				
			</tr>												
		</table>
		<input type="submit" value="Sign Up" />
		</form>

</div>
</div>

</div>

<div id="footer">

 <table align="center">
	  <tr>
		  <td align="center"> <a href="#"> About us </a> </td>
		  <td align="center"> <a href="#"> Contact us </a> </td>
		  <td align="center"> <a href="#"> Terms and Conditions </a> </td>
	  </tr>
  
	  <tr>
	  	  <td></td>
		  <td align="center">Gator Drive: A Distributed File Storage System </td>
		  <td></td>
	 </tr>
	 </table>
</div>

</div>

</body>
</html>