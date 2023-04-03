# API dell'applicazione
- Tipologia di API: **RESTful**

-------------------------------------------------------
## Registrazione Utente
### localhost:{port}/auth/signup

<details><summary> Request PUT </summary>

```json
{
    "username": "ciccio",
    "password": "password",
    "email": "ciccio@email.it"
}
```
Param    | Restrizioni  | . | . | . 
  --     | -            | - | - | - 
username | NotBlank     | Size Min = 5
email    | NotBlank     | Size Min = 5
password | NotBlank     | Size Min = 5 | Size Max = 15
|

</details>

<details><summary> Response </summary>

```json
{
    "createdAt": "2022-12-12T19:43:35.3340382",
    "updatedAt": "2022-12-12T19:43:35.3340382",
    "id": 1,
    "username": "ciccio",
    "email": "ciccio@email.it",
    "enabled": false,
    "confirmCode": "430f4827-e9a1-4eea-afc6-f16ee8089c71",
    "avatar": null,
    "authorities": [
        {
            "id": 5,
            "authorityName": "ROLE_GUEST"
        }
    ]
}
```
Note: L'utente deve essere convalidato dall'Admin per loggare: _enabled -> true_
</details>





-------------------------------------------------------
## 
### localhost:{port}/

<details open><summary> Request ____ </summary>

```json
{

}
```
Param    | Restrizioni  | . | . | . 
  --     | -            | - | - | - 
___      | ___          | _ | _ | _ 
|

</details>

<details open><summary> Response </summary>

```json
{

}
```
Note: 
</details>







-------------------------------------------------------
## 
### localhost:{port}/

<details open><summary> Request ____ </summary>

```json
{

}
```
Param    | Restrizioni  | . | . | . 
  --     | -            | - | - | - 
___      | ___          | _ | _ | _ 
|

</details>

<details open><summary> Response </summary>

```json
{

}
```
Note: 
</details>











-------------------------------------------------------
## 
### localhost:{port}/

<details open><summary> Request ____ </summary>

```json
{

}
```
Param    | Restrizioni  | . | . | . 
  --     | -            | - | - | - 
___      | ___          | _ | _ | _ 
|

</details>

<details open><summary> Response </summary>

```json
{

}
```
Note: 
</details>


