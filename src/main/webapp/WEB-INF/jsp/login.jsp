<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>login</title>
</head>
<body>

    <form action="user" method="post">
                用户名:<input type="text" name="username" /><br/>
                密&nbsp;码:<input type="password" name="password" /><br/>
     <input type="hidden" name="login" value="login"/>
     <input type="submit" value="登 录"/>
    </form>

</body>
</html>