// package com.boxpro.controller;

// import com.boxpro.entity.Usuario;
// import com.boxpro.service.AuthService;
// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.media.Content;
// import io.swagger.v3.oas.annotations.media.ExampleObject;
// import io.swagger.v3.oas.annotations.responses.ApiResponse;
// import io.swagger.v3.oas.annotations.responses.ApiResponses;
// import io.swagger.v3.oas.annotations.security.SecurityRequirement;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.web.bind.annotation.CrossOrigin;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import java.util.HashMap;
// import java.util.Map;

// @RestController
// @RequestMapping("/test")
// @CrossOrigin(origins = "*")
// @Tag(name = "Testes", description = "Endpoints para testar funcionalidades da API")
// public class TestController {

//     @Autowired
//     private AuthService authService;

//     @Operation(
//         summary = "Endpoint público",
//         description = "Endpoint de teste que não requer autenticação"
//     )
//     @ApiResponses(value = {
//         @ApiResponse(
//             responseCode = "200", 
//             description = "Acesso público bem-sucedido",
//             content = @Content(
//                 mediaType = "application/json",
//                 examples = @ExampleObject(
//                     value = "{\"message\":\"Este é um endpoint público - sem autenticação necessária\",\"status\":\"success\"}"
//                 )
//             )
//         )
//     })
//     @GetMapping("/public")
//     public ResponseEntity<Map<String, String>> publicEndpoint() {
//         Map<String, String> response = new HashMap<>();
//         response.put("message", "Este é um endpoint público - sem autenticação necessária");
//         response.put("status", "success");
//         return ResponseEntity.ok(response);
//     }

//     @Operation(
//         summary = "Endpoint protegido",
//         description = "Endpoint que requer autenticação JWT válida"
//     )
//     @ApiResponses(value = {
//         @ApiResponse(
//             responseCode = "200", 
//             description = "Acesso autorizado",
//             content = @Content(
//                 mediaType = "application/json",
//                 examples = @ExampleObject(
//                     value = "{\"message\":\"Este é um endpoint protegido - autenticação necessária\",\"usuario\":\"Administrador\",\"email\":\"admin@boxpro.com\",\"tipo\":\"ADMIN\",\"status\":\"success\"}"
//                 )
//             )
//         ),
//         @ApiResponse(
//             responseCode = "401", 
//             description = "Token não fornecido ou inválido",
//             content = @Content(
//                 mediaType = "application/json",
//                 examples = @ExampleObject(
//                     value = "{\"message\":\"Token inválido ou não fornecido\",\"status\":\"error\"}"
//                 )
//             )
//         )
//     })
//     @SecurityRequirement(name = "Bearer Authentication")
//     @GetMapping("/protected")
//     public ResponseEntity<Map<String, Object>> protectedEndpoint() {
//         Usuario usuario = authService.getCurrentUser();
        
//         Map<String, Object> response = new HashMap<>();
//         response.put("message", "Este é um endpoint protegido - autenticação necessária");
//         response.put("usuario", usuario.getNome());
//         response.put("email", usuario.getEmail());
//         response.put("tipo", usuario.getTipoUsuario());
//         response.put("status", "success");
        
//         return ResponseEntity.ok(response);
//     }

//     @Operation(
//         summary = "Endpoint exclusivo para ADMIN",
//         description = "Endpoint que requer autenticação e permissão de ADMIN"
//     )
//     @ApiResponses(value = {
//         @ApiResponse(
//             responseCode = "200", 
//             description = "Acesso autorizado para ADMIN",
//             content = @Content(
//                 mediaType = "application/json",
//                 examples = @ExampleObject(
//                     value = "{\"message\":\"Este é um endpoint apenas para ADMIN\",\"status\":\"success\"}"
//                 )
//             )
//         ),
//         @ApiResponse(
//             responseCode = "403", 
//             description = "Acesso negado - usuário não é ADMIN",
//             content = @Content(
//                 mediaType = "application/json",
//                 examples = @ExampleObject(
//                     value = "{\"message\":\"Access Denied\",\"status\":400}"
//                 )
//             )
//         )
//     })
//     @SecurityRequirement(name = "Bearer Authentication")
//     @GetMapping("/admin")
//     @PreAuthorize("hasRole('ADMIN')")
//     public ResponseEntity<Map<String, String>> adminEndpoint() {
//         Map<String, String> response = new HashMap<>();
//         response.put("message", "Este é um endpoint apenas para ADMIN");
//         response.put("status", "success");
//         return ResponseEntity.ok(response);
//     }

//     @Operation(
//         summary = "Endpoint para FUNCIONARIO e ADMIN",
//         description = "Endpoint que requer permissão de FUNCIONARIO ou ADMIN"
//     )
//     @SecurityRequirement(name = "Bearer Authentication")
//     @GetMapping("/funcionario")
//     @PreAuthorize("hasRole('FUNCIONARIO') or hasRole('ADMIN')")
//     public ResponseEntity<Map<String, String>> funcionarioEndpoint() {
//         Map<String, String> response = new HashMap<>();
//         response.put("message", "Este é um endpoint para FUNCIONARIO e ADMIN");
//         response.put("status", "success");
//         return ResponseEntity.ok(response);
//     }

//     @Operation(
//         summary = "Endpoint para todos os usuários autenticados",
//         description = "Endpoint acessível por CLIENTE, FUNCIONARIO e ADMIN"
//     )
//     @SecurityRequirement(name = "Bearer Authentication")
//     @GetMapping("/cliente")
//     @PreAuthorize("hasRole('CLIENTE') or hasRole('FUNCIONARIO') or hasRole('ADMIN')")
//     public ResponseEntity<Map<String, String>> clienteEndpoint() {
//         Map<String, String> response = new HashMap<>();
//         response.put("message", "Este é um endpoint para todos os tipos de usuário");
//         response.put("status", "success");
//         return ResponseEntity.ok(response);
//     }
// }