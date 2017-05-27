package de.flapdoodle.solid.theme.mustache;

import java.util.List;
import java.util.Optional;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.samskivert.mustache.DefaultCollector;
import com.samskivert.mustache.Mustache.VariableFetcher;

import de.flapdoodle.solid.types.Maybe;
import de.flapdoodle.solid.types.reflection.Inspector;
import de.flapdoodle.solid.types.tree.PropertyTree;
import de.flapdoodle.types.Either;

final class CustomCollector extends DefaultCollector {
		/**
		 * 
		 */
		private final MustacheTheme mustacheTheme;

		/**
		 * @param mustacheTheme
		 */
		CustomCollector(MustacheTheme mustacheTheme) {
			this.mustacheTheme = mustacheTheme;
		}

		@Override
		public VariableFetcher createFetcher(Object ctx, String name) {
			return wrapWithMaybeAndOptionalExtractor(internal(ctx, name));
		}

		private VariableFetcher internal(Object ctx, String name) {
			try {
//					Preconditions.checkArgument(!name.isEmpty(),"you should not use something like {{ .Foo }}");
				if (name.isEmpty()) {
					return (c,n) -> c;
				}
				
				VariableFetcher ret = super.createFetcher(ctx, name);
				if (ret==null) {
//						System.out.println(""+ctx.getClass()+".'"+name+"'");
					if ("*".equals(name)) {
						return (c,n) -> Inspector.propertyNamesOf(c.getClass());
					}
					if (ctx instanceof MapLike) {
						Maybe<Object> value = ((MapLike) ctx).get(name);
						if (value.isPresent()) {
							return (c,n) -> value.get();
						}
					}
					if (ctx instanceof PropertyTree) {
						return (c,n) -> ((PropertyTree) c).get(n);
					}
					if (ctx instanceof MustacheFormating) {
						ImmutableMap<String, de.flapdoodle.solid.formatter.Formatter> map = Preconditions.checkNotNull(this.mustacheTheme.formatter.get(),"formatter map not set");
						de.flapdoodle.solid.formatter.Formatter formatter = map.get(name);
						return (c,n) ->formatter.format(((MustacheFormating) c).value()).orElse(() -> "");
					}
					if (name.equals("formatWith")) {
						return (c,n) -> CustomCollector.singleValue(c).map(MustacheFormating::of).orElse(null);
					}
				}
				return ret;
			} catch (RuntimeException rx) {
				throw new RuntimeException("ctx.class: "+ctx.getClass()+", name: '"+name+"'",rx);
			}
		}

		private VariableFetcher wrapWithMaybeAndOptionalExtractor(VariableFetcher src) {
			if (src!=null) {
				return (ctx,name) -> {
					Object ret = src.get(ctx, name);
					if (ret instanceof Maybe) {
						Maybe maybe = (Maybe) ret;
						return maybe.isPresent() ? maybe.get() : null;
					}
					if (ret instanceof Optional) {
						Optional maybe = (Optional) ret;
						return maybe.isPresent() ? maybe.get() : null;
					}
					if (ret instanceof com.google.common.base.Optional) {
						com.google.common.base.Optional maybe = (com.google.common.base.Optional) ret;
						return maybe.isPresent() ? maybe.get() : null;
					}
					return ret;
				};
			}
			return src;
		}

		protected static Maybe<Object> singleValue(Object c) {
			if (c instanceof List) {
				List l=(List) c;
				if (l.size()==1) {
					return singleValue(l.get(0));
				}
				return Maybe.empty();
			}
			if (c instanceof Either) {
				Either e=(Either) c;
				return e.isLeft() ? singleValue(e.left()) : singleValue(e.right());
			}
			return Maybe.ofNullable(c);
		}
	}