package com.example.context;

import org.reactivestreams.Subscriber;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoOperator;
import reactor.util.context.Context;

import org.springframework.web.server.ServerWebExchange;

/**
 * @author Stephane Maldini
 */
final class MonoWebExchangeContext extends MonoOperator<Void, Void> {

	final ServerWebExchange exchange;

	MonoWebExchangeContext(Mono<Void> m, ServerWebExchange exchange) {
		super(m);
		this.exchange = exchange;
	}

	@Override
	public void subscribe(Subscriber<? super Void> actual, Context context) {
		source.subscribe(actual, context.put(ServerWebExchange.class, exchange));
	}
}
