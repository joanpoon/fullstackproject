<!DOCTYPE html>

<html lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="utf-8" />
    <title>Login</title>
	<meta name="description" content="">
	<meta name="author" content="whidbey">
	
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <link rel="stylesheet" href="util/CSS/siteTheme.css" type="text/css">
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
 
 
    
</head>
<body>
 <header class="headstyle">
    <section>
   
   
        <a class="login" href="/airplane/tracker?cmd=login">Log in</a>
        <br>
        <a class="login" href="/airplane/tracker?cmd=register">Sign up</a>
	
    <address>Lake Washington Institute of Tecnology, Kirkland, WA, USA</address>
    </section>
  	
</header>




    <!--About Section-->
    <section class="for-full-back color-light " id="about">
        <div class="container">

            <div class="row text-center">
                <div class="col-md-8 col-md-offset-2">
                    <h1>Welcome to Airplane-Tracker</h1>
                  
                </div>

            </div>
            <div class="row text-center">

                 <legend><b>Login</b></legend>
                <form method="post" action="tracker?cmd=login" class="formProduct">
                    <label for="username"><b>User Name</b></label>
                    <input type="text" name="username" id="username">
                    </br>
                    <label for="pass"><b>Password</b></label>
                    <input type="password" name="pass" id="pass">
                    </br>
                    <input type="submit" id="login" value="Login" data-icon="check" data-iconpos="right" data-inline="true">
                    <input type="button" id="cancel" value="Cancel" data-icon="check" data-iconpos="right" data-inline="true" onclick="location.href='/airplane/tracker?cmd=home'">
                </form>

            </div>
             
            
       


    </section>  
          
       
    
   
<footer> 
   
		<p id="members">Michael Morrow | Chun Yin Douglas Chan | Joan Poon | Javier Sarmiento</p>
      
			
   
<address>Lake Washington Institute of Tecnology, Kirkland, WA, USA</address>
</footer>

</body>

</html>
