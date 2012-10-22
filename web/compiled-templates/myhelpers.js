Handlebars.registerHelper('compare',
		function(lvalue, operator, rvalue, options) {

			var operators, result;

			if (arguments.length < 3) {
				throw new Error(
						"Handlerbars Helper 'compare' needs 2 parameters");
			}

			if (options === undefined) {
				options = rvalue;
				rvalue = operator;
				operator = "===";
			}

			operators = {
				'==' : function(l, r) {
					return l == r;
				},
				'===' : function(l, r) {
					return l === r;
				},
				'!=' : function(l, r) {
					return l != r;
				},
				'!==' : function(l, r) {
					return l !== r;
				},
				'<' : function(l, r) {
					return l < r;
				},
				'>' : function(l, r) {
					return l > r;
				},
				'<=' : function(l, r) {
					return l <= r;
				},
				'>=' : function(l, r) {
					return l >= r;
				},
				'typeof' : function(l, r) {
					return typeof l == r;
				}
			};

			if (!operators[operator]) {
				throw new Error(
						"Handlerbars Helper 'compare' doesn't know the operator "
								+ operator);
			}

			result = operators[operator](lvalue, rvalue);

			if (result) {
				return options.fn(this);
			} else {
				return options.inverse(this);
			}

		});

Handlebars.loadPartial = function(name) {
	var partial = Handlebars.partials[name];
	if (typeof partial === "string") {
		partial = Handlebars.compile(partial);
		Handlebars.partials[name] = partial;
	}
	return partial;
};

Handlebars.registerHelper("block", function(name, options) {
	/* Look for partial by name. */
	var partial = Handlebars.loadPartial(name) || options.fn;
	return partial(this, {
		data : options.hash
	});
});

Handlebars.registerHelper("partial", function(name, options) {
	Handlebars.registerPartial(name, options.fn);
});

// format an ISO date using Moment.js
// http://momentjs.com/
// moment syntax example: moment(Date("2011-07-18T15:50:52")).format("MMMM
// YYYY")
// usage: {{dateFormat creation_date format="MMMM YYYY"}}
Handlebars.registerHelper('dateFormat', function(context, block) {
	if (window.moment) {
		var f = block.hash.format || "dddd, D MMMM YYYY, h:mm:ss a";
		return moment(Date(context)).format(f);
	} else {
		return context; // moment plugin not available. return data as is.
	}
	;
});

Handlebars.registerHelper('encodeId', function(context, block) {
	return encodeURIComponent(context);
});
