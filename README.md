##Reponse POST avec ou sans body

Suite à une discussion avec [@nogunner](https://twitter.com/nogunner), [@LudovicLafole](https://twitter.com/LudovicLafole) et [@cestpasdur](https://twitter.com/cestpasdur) sur le channel TourJS du [slack dev Tours](https://devtours.slack.com/messages) concernant le type de réponse attendu de la méthode POST d'une API RESTFul j'ai pondu ce petit POC JAX-RS.
 
 Si on suit les recommandation de la "spec" RESTFul, une méthode ```POST``` ne doit renvoyer une réponse sans body avec dans les header le champs location qui est l'URL d'acces à la ressource via le verbe ```GET```.
 Car on part du principe que l'ensemble des données de la ressource créée (hormis l'id), se trouve déjà du côté client, ou que l'appel de la ressource identifié par son URI est peu couteuse.
 
 Cependant [@LudovicLafole](https://twitter.com/LudovicLafole) m'a fait remarqué, fort justement, que cela n'avait de valeur dans le cas seulement ou la latence du réseau et le temps de transfert étaient faible...
 
 Mais dans le cas d'un developpement d'une API succeptible d'être intérrogée par une application mobile dependante de la qualité du reseau, ce type de considération pouvait avoir son importance.
  
 C'est le sujet de ce petit POC, comment faire en sorte de moduler le comportement de la réponse à l'appel d'une méthode ```POST``` de notre API REST.
 
 Le use case est simple : 
 
 * appel sans demande de content-type de retour (champs ```accept``` du header de la requête), comportement attendu selon les "spec" RESTFul
 * appel en indiquant le type de contenu attendu (header ```accept:application/json```), la ressource créée est renvoyé dans le corp de la réponse en mêm temps que le champs location est renseigné.
 
 ---
 
 ####Implémentation JAX-RS
 
 Dans le cadre d'un developpement JAX-RS (spec REST de JavaEE, mais aussi applicable sur un dev Spring[boot]), il suffit d'injecter les headers de la requete au niveau de votre méthode pointé par le verbe ```POST```
 
 ```java
 @POST
   @Consumes("application/x-www-form-urlencoded")
   public Response createUser(@Context HttpHeaders httpHeaders, @FormParam("username") String username) {
     User user = new User();
     // Création de l'utilisateur          
    return Response
        .created(UriBuilder.fromResource(UserEndPoint.class).path(user.getId()).build())
        .entity(httpHeaders.getAcceptableMediaTypes().contains(MediaType.WILDCARD_TYPE) ? null : user)
        .build();
   }
 ``` 

***Explication*** : 

L'injection des header se fait via l'annotation ```@Context``` qui permet l'injection d'ojet décrivant la requête entrante (header, corps de la requête, etc...). Dans notre cas, les headers, nous allons avoir accès au format de donnée attendu (```AcceptableMediaTypes```).

Je test si aucun format n'est attendu (```MediaType.WILDCARD_TYPE``` qui correspond à aucun format demandé ou ```"*/*"```). Si un format est demandé, alors je fourni l'entité à la réponse et je laisse les formatteur du framework faire leur boulot. :)

Pour les appels :

#####Appel sans demande spécifique de format de réponse

```shell
http -f POST :8080/api/user username='machin'
```

le résultat sera :

```
HTTP/1.1 201 Created
Connection: keep-alive
Content-Length: 0
Date: Thu, 20 Oct 2016 22:07:57 GMT
Location: http://localhost:8080/api/user/1644221
Server: WildFly/10
X-Powered-By: Undertow/1
```

#####Appel avec demade spécifique de format de réponse (```application/json```)

```shell
http -f POST :8080/api/user username='machin' accept:'application/json'
```

le résulat sera : 
```
HTTP/1.1 201 Created
Connection: keep-alive
Content-Length: 94
Content-Type: application/json
Date: Thu, 20 Oct 2016 22:09:21 GMT
Location: http://localhost:8080/api/user/1644221
Server: WildFly/10
X-Powered-By: Undertow/1

{
    "creationDate": "2016-10-21",
    "expirationDate": "2016-12-21",
    "id": "1644221",
    "username": "machin"
}
```

#####Appel avec demade spécifique de format de réponse (```application/xml```)

```shell
http -f POST :8080/api/user username='machin' accept:'application/xml'
```

le résulat sera : 
```
HTTP/1.1 201 Created
Connection: keep-alive
Content-Length: 190
Content-Type: application/xml
Date: Thu, 20 Oct 2016 22:18:16 GMT
Location: http://localhost:8080/api/user/1644221
Server: WildFly/10
X-Powered-By: Undertow/1

<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<user id="1644221">
    <username>machin</username>
    <creationDate>2016-10-21</creationDate>
    <expirationDate>2016-12-21</expirationDate>
</user>

```

---

*Pour les appel, j'utilise [HTTPie](https://github.com/jkbrzt/httpie), c'est quand même plus lisible qu'avec un cURL des familles* :)
