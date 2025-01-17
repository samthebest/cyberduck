package ch.cyberduck.core.brick;

/*
 * Copyright (c) 2002-2019 iterate GmbH. All rights reserved.
 * https://cyberduck.io/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

import ch.cyberduck.core.Credentials;
import ch.cyberduck.core.DefaultIOExceptionMappingService;
import ch.cyberduck.core.Host;
import ch.cyberduck.core.PasswordCallback;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.exception.LoginCanceledException;
import ch.cyberduck.core.exception.LoginFailureException;
import ch.cyberduck.core.http.DefaultHttpResponseExceptionMappingService;
import ch.cyberduck.core.preferences.PreferencesFactory;
import ch.cyberduck.core.shared.AbstractSchedulerFeature;
import ch.cyberduck.core.threading.CancelCallback;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class BrickPairingSchedulerFeature extends AbstractSchedulerFeature<Credentials> {
    private static final Logger log = Logger.getLogger(BrickPairingSchedulerFeature.class);

    private final BrickSession session;
    private final String token;
    private final Host host;
    private final CancelCallback cancel;

    public BrickPairingSchedulerFeature(final BrickSession session, final String token, final Host host, final CancelCallback cancel) {
        super(1000L);
        this.session = session;
        this.token = token;
        this.host = host;
        this.cancel = cancel;
    }

    @Override
    protected Credentials operate(final PasswordCallback callback, final Path file) throws BackgroundException {
        // Query status
        try {
            final HttpPost resource = new HttpPost(String.format("https://app.files.com/api/rest/v1/sessions/pairing_key/%s", token));
            resource.setHeader(HttpHeaders.ACCEPT, "application/json");
            resource.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            if(log.isInfoEnabled()) {
                log.info(String.format("Fetch credentials for paring key %s from %s", token, resource));
            }
            final JsonObject json = session.getClient().execute(resource, new AbstractResponseHandler<JsonObject>() {
                @Override
                public JsonObject handleEntity(final HttpEntity entity) throws IOException {
                    final ByteArrayOutputStream out = new ByteArrayOutputStream();
                    IOUtils.copy(entity.getContent(), out);
                    final JsonParser parser = new JsonParser();
                    return parser.parse(new InputStreamReader(new ByteArrayInputStream(out.toByteArray()))).getAsJsonObject();
                }
            });
            final Credentials credentials = host.getCredentials();
            if(json.has("username")) {
                if(StringUtils.isBlank(credentials.getUsername())) {
                    credentials.setUsername(json.getAsJsonPrimitive("username").getAsString());
                }
                else {
                    if(StringUtils.equals(credentials.getUsername(), json.getAsJsonPrimitive("username").getAsString())) {
                        log.warn(String.format("Mismatch of username. Previously authorized as %s and now paired as %s",
                            credentials.getUsername(), json.getAsJsonPrimitive("username").getAsString()));
                        callback.close(null);
                        throw new LoginCanceledException();
                    }
                }
            }
            else {
                throw new LoginFailureException(String.format("Invalid response for pairing key %s", token));
            }
            if(json.has("password")) {
                credentials.setPassword(json.getAsJsonPrimitive("password").getAsString());
            }
            else {
                throw new LoginFailureException(String.format("Invalid response for pairing key %s", token));
            }
            if(json.has("nickname")) {
                if(PreferencesFactory.get().getBoolean("brick.pairing.nickname.configure")) {
                    host.setNickname(json.getAsJsonPrimitive("nickname").getAsString());
                }
            }
            if(json.has("server")) {
                if(PreferencesFactory.get().getBoolean("brick.pairing.hostname.configure")) {
                    host.setHostname(URI.create(json.getAsJsonPrimitive("server").getAsString()).getHost());
                }
            }
            callback.close(credentials.getUsername());
            return credentials;
        }
        catch(HttpResponseException e) {
            switch(e.getStatusCode()) {
                case HttpStatus.SC_NOT_FOUND:
                    log.warn(String.format("Missing login for pairing key %s", token));
                    cancel.verify();
                    break;
                default:
                    throw new DefaultHttpResponseExceptionMappingService().map(e);
            }
        }
        catch(IOException e) {
            throw new DefaultIOExceptionMappingService().map(e);
        }
        return null;
    }
}
