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
<body >
 <header class="headstyle">
    <section>
   

    <a class="login" href="/airplane/tracker?cmd=searchLogged">Go to Map</a>
        <br>
       
	<p name="username" value="${username}">${username}</p>
    </section>
  	
</header>


    <section class="for-full-back color-light " id="about">
        <div class="container">

            <div class="row text-center">
                <div class="col-md-8 col-md-offset-2">
                    <h1>My Aircrafts</h1>
                  <div class="row text-center">

                    <form action="/airplane/tracker?cmd=saveFlights" method="post" >

                    <#list InterestingFlight as InterestingFlight>
                        <li id="${InterestingFlight}" type="checkbox" /> ID: ${InterestingFlight} </li>
                        </br>
                     </#list>

                     

                    </form>
                    <br />
               

            </div>
            </div>

        </div>
            
    </section>  
          
       
    
   
<footer> 
   
		<p id="members">Michael Morrow | Chun Yin Douglas Chan | Joan Poon | Javier Sarmiento</p>
      
			
   
<address>Lake Washington Institute of Technology, Kirkland, WA, USA</address>
</footer>


</body>

</html>

