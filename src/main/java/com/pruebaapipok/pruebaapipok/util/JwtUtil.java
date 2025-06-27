package com.pruebaapipok.pruebaapipok.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    // La clave secreta para firmar y verificar tokens, inyectada desde application.properties
    @Value("${jwt.secret}")
    private String SECRET;

    /**
     * Genera un token JWT para el usuario proporcionado.
     * @param userDetails Los detalles del usuario para los que se genera el token.
     * @return El token JWT generado.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Crea el token JWT con los claims, sujeto (nombre de usuario),
     * fecha de emisión y fecha de expiración.
     * @param claims Claims adicionales para incluir en el token.
     * @param userName El nombre de usuario (sujeto) del token.
     * @return El token JWT compacto y firmado.
     */
    private String createToken(Map<String, Object> claims, String userName) {
        // El token expira después de 10 horas (1000 ms * 60 s * 60 min * 10 h)
        long expirationTime = System.currentTimeMillis() + 1000 * 60 * 60 * 10;

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(expirationTime))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Obtiene la clave de firma a partir de la cadena secreta.
     * Asume que la clave secreta está codificada en Base64 URL-safe.
     * @return La clave de firma.
     */
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extrae el nombre de usuario (sujeto) del token JWT.
     * @param token El token JWT.
     * @return El nombre de usuario.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae la fecha de expiración del token JWT.
     * @param token El token JWT.
     * @return La fecha de expiración.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae un claim específico del token JWT utilizando una función resolutora.
     * @param token El token JWT.
     * @param claimsResolver Función para resolver el claim deseado.
     * @param <T> Tipo del claim.
     * @return El valor del claim.
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae todos los claims del token JWT.
     * Incluye manejo de excepciones para diferentes escenarios de error del JWT.
     * @param token El token JWT.
     * @return Los claims del token.
     * @throws ExpiredJwtException Si el token ha expirado.
     * @throws SignatureException Si la firma del token es inválida.
     * @throws MalformedJwtException Si el token está mal formado.
     * @throws IllegalArgumentException Si el token es nulo o vacío.
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // El token ha expirado. Puedes loggear o lanzar una excepción personalizada.
            System.err.println("JWT expirado: " + e.getMessage());
            throw e; // Relanza para que sea manejado por un filtro o ControllerAdvice
        } catch (SignatureException e) {
            // La firma del token no es válida.
            System.err.println("Firma JWT inválida: " + e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            // El token no está bien formado.
            System.err.println("JWT mal formado: " + e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            // Token nulo o cadena vacía.
            System.err.println("Argumento inválido para JWT: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            // Cualquier otra excepción inesperada.
            System.err.println("Error inesperado al procesar JWT: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Valida si un token JWT es válido para un usuario dado.
     * @param token El token JWT a validar.
     * @param userDetails Los detalles del usuario a comparar.
     * @return true si el token es válido y no ha expirado para el usuario, false en caso contrario.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Verifica si el token JWT ha expirado.
     * @param token El token JWT.
     * @return true si el token ha expirado, false en caso contrario.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}