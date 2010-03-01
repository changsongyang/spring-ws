/*
 * Copyright 2005-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.ws.soap.security.wss4j.callback;

import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.userdetails.User;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;

import org.apache.ws.security.WSUsernameTokenPrincipal;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;

/** @author tareq */
public class SpringDigestPasswordValidationCallbackHandlerTest {

    private SpringDigestPasswordValidationCallbackHandler callbackHandler;

    private GrantedAuthorityImpl grantedAuthority;

    private UsernameTokenPrincipalCallback callback;

    private UserDetails user;

    @Before
    public void setUp() throws Exception {
        callbackHandler = new SpringDigestPasswordValidationCallbackHandler();

        grantedAuthority = new GrantedAuthorityImpl("ROLE_1");
        user = new User("Ernie", "Bert", true, true, true, true, new GrantedAuthority[]{grantedAuthority});

        WSUsernameTokenPrincipal principal = new WSUsernameTokenPrincipal("Ernie", true);
        callback = new UsernameTokenPrincipalCallback(principal);
    }

    @Test
    public void testHandleUsernameTokenPrincipal() throws Exception {
        UserDetailsService userDetailsService = createMock(UserDetailsService.class);
        callbackHandler.setUserDetailsService(userDetailsService);

        expect(userDetailsService.loadUserByUsername("Ernie")).andReturn(user).anyTimes();

        replay(userDetailsService);

        callbackHandler.handleUsernameTokenPrincipal(callback);
        SecurityContext context = SecurityContextHolder.getContext();
        Assert.assertNotNull("SecurityContext must not be null", context);
        Authentication authentication = context.getAuthentication();
        Assert.assertNotNull("Authentication must not be null", authentication);
        GrantedAuthority[] authorities = authentication.getAuthorities();
        Assert.assertTrue("GrantedAuthority[] must not be null or empty",
                (authorities != null && authorities.length > 0));
        Assert.assertEquals("Unexpected authority", grantedAuthority, authorities[0]);

        verify(userDetailsService);
    }
}
