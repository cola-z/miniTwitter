<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%> <!-- jsp2.5默认el表达式关闭，需要打开该功能 -->
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>twitter user profile</title>
<script type="text/javascript">
    function test(data) {
    	/* var tags = document.getElementsByTagName("span");
    	for(var i=0; i < tags.length; i++) {
    		if(tags[i].className == "uid") {
    			alert(tags[i].innerText);
    		}
    	} */
    	window.location.href = "http://localhost:8888/twitter/user?uid=" + data;
    	//alert(data);
    }
</script>
</head>
<body>
<div>
<div>${user.userName} <a href="user?logout=logout">退出</a></div>
<div>已关注的人：${user.following}</div>
<div>关注TA的人:${user.followers}</div>
<div>发推数:${user.posts}</div>
<div>
            你的推文
    <c:forEach items="${messages}" var="maps">
    <div><c:forEach items="${maps}" var="map">
    ${map.key}-->${map.value}
    </c:forEach>
    </div>
    </c:forEach>
</div>

<div><form action="message" method="post"><textarea name="message" rows="5" cols="40"></textarea>
    <input type="submit" value="发 推"/>
</form></div>

<div>其他人的推文
    <c:forEach items="${otherMessages}" var="maps">
    <div><c:forEach items="${maps}" var="map">
    <c:if test="${map.key == 'uid'}"><c:set var="puid" value="${map.value}"/></c:if>
    <span class="${map.key}">${map.value}</span>&nbsp;
    </c:forEach>
    </div>
    <input type="button" value="关 注" onclick="test(${puid})"/>
    </c:forEach>
</div>
</div>


</body>
</html>