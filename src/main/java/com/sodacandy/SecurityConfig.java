//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
//==============================================================================
// CURSO : APLICACIONES WEB Y PATRONES
// PROF: WILBERT MOLINA
// ENTREGABLE:PROYECTO
// GRUPO:6
// INTEGRANTES:RONALD CALDERON, SHEILYN CORDOBA, MICHELLE GUERRERO, SOFIA LOAIZA
//==============================================================================
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        //Rutas publicas (sitio del cliente + login + recuperar clave) vs.
        //rutas del staff, que exigen haber iniciado sesion.
        //TODO equipo: cuando definan que rol ve cada modulo, cambien
        //"authenticated()" por ".hasRole(\"ADMINISTRADOR\")" etc. en cada linea.
        http.authorizeHttpRequests(request -> request
                .requestMatchers("/webjars/**", "/css/**", "/js/**", "/img/**", "/fav/**").permitAll()
                .requestMatchers("/", "/index", "/menu/**", "/login", "/acceso/**", "/acceso_denegado").permitAll()
                .anyRequest().authenticated()
        );

        //Se establece el proceso para hacer "login"
        http.formLogin(login -> login
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/home", true)
                .failureUrl("/login?error=true")
                .permitAll()
        );

        //Se establece el proceso para hacer "logout"
        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
        );

        //Pagina cuando alguien logueado intenta entrar a algo que no le corresponde
        http.exceptionHandling(ex -> ex.accessDeniedPage("/acceso_denegado"));

        //Una sola sesion activa por usuario
        http.sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
        );

        return http.build();
    }

    //Encripta y valida las contraseñas con BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //Conecta Spring Security con nuestro UsuarioDetailsService y el encoder
    @Autowired
    public void configurerGlobal(AuthenticationManagerBuilder build,
            @Lazy PasswordEncoder passwordEncoder,
            @Lazy UserDetailsService userDetailsService) throws Exception {
        build.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }
}