package io.vertx.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> promise) throws Exception {

    Promise<String> dbVerticleDeployment = Promise.promise();  // <1>
    vertx.deployVerticle(new WikiDatabaseVerticle(), dbVerticleDeployment);  // <2>

    dbVerticleDeployment.future().compose(id -> {  // <3>

      Promise<String> httpVerticleDeployment = Promise.promise();
      vertx.deployVerticle(
        "io.vertx.starter.HttpServerVerticle",  // <4>
        new DeploymentOptions().setInstances(2),    // <5>
        httpVerticleDeployment);

      return httpVerticleDeployment.future();  // <6>

    }).onComplete(ar -> {   // <7>
      if (ar.succeeded()) {
        promise.complete();
      } else {
        promise.fail(ar.cause());
      }
    });
  }
}
