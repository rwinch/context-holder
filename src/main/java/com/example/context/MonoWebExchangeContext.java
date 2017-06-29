package com.example.context;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoOperator;
import reactor.util.context.Context;
import reactor.util.context.Contextualized;

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
		Context c = context.put(ServerWebExchange.class, exchange);
		source.subscribe(new WebExchangeSubscriber(actual, c), c);
	}

	final static class WebExchangeSubscriber implements Subscriber<Void>, Contextualized {
		final Subscriber<? super Void> actual;
		final Context c;

		WebExchangeSubscriber(Subscriber<? super Void> actual, Context c) {
			this.actual = actual;
			this.c = c;
		}

		@Override
		public void onSubscribe(Subscription s) {
			actual.onSubscribe(s);
		}

		@Override
		public void onNext(Void aVoid) {
		}

		@Override
		public void onError(Throwable t) {
			actual.onError(t);
		}

		@Override
		public void onComplete() {
			actual.onComplete();
		}

		@Override
		public Context currentContext() {
			return c;
		}
	}
}
