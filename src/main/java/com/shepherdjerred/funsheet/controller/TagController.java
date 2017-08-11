package com.shepherdjerred.funsheet.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shepherdjerred.funsheet.objects.Tag;
import com.shepherdjerred.funsheet.objects.User;
import com.shepherdjerred.funsheet.payloads.EditTagPayload;
import com.shepherdjerred.funsheet.payloads.NewTagPayload;
import com.shepherdjerred.funsheet.storage.Store;
import lombok.extern.log4j.Log4j2;

import java.util.Optional;
import java.util.UUID;

import static spark.Spark.*;

@Log4j2
public class TagController implements Controller {

    private Store store;
    private static ObjectMapper objectMapper = new ObjectMapper();

    public TagController(Store store) {
        this.store = store;
    }

    @Override
    public void setupRoutes() {
        get("/api/tags", (request, response) -> {
            response.type("application/json");

            return objectMapper.writeValueAsString(store.getTags());
        });

        get("/api/tags/:tag", (request, response) -> {
            response.type("application/json");

            String tagParam = request.params().get(":tag");
            UUID tagUuid = UUID.fromString(tagParam);
            Optional<Tag> tag = store.getTag(tagUuid);

            if (tag.isPresent()) {
                return objectMapper.writeValueAsString(tag.get());
            } else {
                response.status(404);
                return "";
            }
        });

        post("/api/tags", (request, response) -> {
            response.type("application/json");

            NewTagPayload tagPayload = objectMapper.readValue(request.body(), NewTagPayload.class);

            ProcessBuilder processBuilder = new ProcessBuilder();
            Algorithm algorithm = Algorithm.HMAC256(processBuilder.environment().get("JWT_SECRET"));
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("http://funsheet.herokuapp.com")
                    .build();
            DecodedJWT jwt = verifier.verify(tagPayload.getJwt());

            UUID userUuid = store.getUserUuid(jwt.getClaim("username").asString());
            Optional<User> userToAuthTo = store.getUser(userUuid);

            if (!userToAuthTo.isPresent()) {
                response.status(401);
                return "";
            }

            if (!userToAuthTo.get().authenticate(jwt.getClaim("password").asString())) {
                response.status(401);
                return "";
            }

            if (!tagPayload.isValid()) {
                response.status(400);
                return "";
            }

            if (store.isTagNameTaken(tagPayload.getName())) {
                response.status(400);
                return "";
            }

            Tag tag = new Tag(
                    tagPayload.getName(),
                    UUID.randomUUID()
            );

            store.addTag(tag);

            return objectMapper.writeValueAsString(tag);
        });

        patch("/api/tags/:tag", (request, response) -> {
            response.type("application/json");

            EditTagPayload tagPayload = objectMapper.readValue(request.body(), EditTagPayload.class);

            ProcessBuilder processBuilder = new ProcessBuilder();
            Algorithm algorithm = Algorithm.HMAC256(processBuilder.environment().get("JWT_SECRET"));
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("http://funsheet.herokuapp.com")
                    .build();
            DecodedJWT jwt = verifier.verify(tagPayload.getJwt());

            UUID userUuid = store.getUserUuid(jwt.getClaim("username").asString());
            Optional<User> userToAuthTo = store.getUser(userUuid);

            if (!userToAuthTo.isPresent()) {
                response.status(401);
                return "";
            }

            if (!userToAuthTo.get().authenticate(jwt.getClaim("password").asString())) {
                response.status(401);
                return "";
            }

            if (!tagPayload.isValid()) {
                response.status(400);
                return "";
            }

            Optional<Tag> tagOptional = store.getTag(tagPayload.getUuid());

            if (tagOptional.isPresent()) {
                Tag tag = tagOptional.get();
                if (tagPayload.getName() != null) {
                    if (store.isTagNameTaken(tagPayload.getName()) && !tag.getName().equals(tagPayload.getName())) {
                        response.status(400);
                        return "";
                    }
                    tag.setName(tagPayload.getName());
                }

                store.updateTag(tag);

                return objectMapper.writeValueAsString(tag);
            } else {
                response.status(400);
                return "";
            }

        });

        delete("/api/tags/:tag", (request, response) -> {
            response.type("application/json");

            // TODO auth

            String tagParam = request.params().get(":tag");
            UUID tagUuid = UUID.fromString(tagParam);

            store.deleteTag(tagUuid);

            return "";
        });
    }
}
