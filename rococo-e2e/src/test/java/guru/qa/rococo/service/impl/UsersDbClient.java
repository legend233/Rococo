package guru.qa.rococo.service.impl;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.auth.AuthUserEntity;
import guru.qa.rococo.data.entity.auth.Authority;
import guru.qa.rococo.data.entity.auth.AuthorityEntity;
import guru.qa.rococo.data.entity.userdata.UserdataEntity;
import guru.qa.rococo.data.repository.AuthUserRepository;
import guru.qa.rococo.data.repository.UserdataRepository;
import guru.qa.rococo.data.repository.impl.AuthUserRepositoryHibernate;
import guru.qa.rococo.data.repository.impl.UserdataRepositoryHibernate;
import guru.qa.rococo.data.tpl.XaTransactionTemplate;
import guru.qa.rococo.model.TestData;
import guru.qa.rococo.model.UserJson;
import guru.qa.rococo.service.UsersClient;
import io.qameta.allure.Step;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class UsersDbClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate();
    private final UserdataRepository userdataRepository = new UserdataRepositoryHibernate();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    @Step("Create user using SQL")
    @Nonnull
    @Override
    public UserJson createUser(String username, String password) {
        return requireNonNull(
                xaTransactionTemplate.execute(
                        () -> UserJson.fromEntity(
                                createNewUser(username, password)
                        ).addTestData(
                                new TestData(
                                        password
                                )
                        )
                )
        );
    }

    @Nonnull
    private UserdataEntity createNewUser(String username, String password) {
        AuthUserEntity authUser = authUserEntity(username, password);
        authUserRepository.create(authUser);
        return userdataRepository.create(userdataEntity(username));
    }

    @Nonnull
    private UserdataEntity userdataEntity(String username) {
        UserdataEntity ue = new UserdataEntity();
        ue.setUsername(username);
        return ue;
    }

    @Nonnull
    private AuthUserEntity authUserEntity(String username, String password) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(username);
        authUser.setPassword(pe.encode(password));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        authUser.setAuthorities(
                Arrays.stream(Authority.values()).map(
                        e -> {
                            AuthorityEntity ae = new AuthorityEntity();
                            ae.setUser(authUser);
                            ae.setAuthority(e);
                            return ae;
                        }
                ).toList()
        );
        return authUser;
    }
}