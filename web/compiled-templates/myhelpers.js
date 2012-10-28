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

var ValueRenderer = {
	render : function(pv) {
		var value = pv.value;
		var datatype = pv.property.datatype;
		var renderingHint = pv.property.renderingHint;

		var ret;

		if (datatype == 'URI') {
			ret = "<a href='" + value + "'>" + value + "</a>";
		} else if ((datatype == 'LONG' || datatype == 'DOUBLE')
				&& renderingHint == 'STORAGE_VOLUME') {
			ret = humanize.filesize(value);
		} else if (datatype == 'DATE') {
			if (renderingHint == 'DATE_DAY') {
				ret = moment(Date(value)).format("dddd, D MMMM YYYY");
			} else {
				ret = moment(Date(value))
						.format("dddd, D MMMM YYYY, h:mm:ss a");
			}
		} else if (datatype == "STRING_DICTIONARY") {
			ret = "<table class='table table-bordered table-condensed' style='margin:0'>";
			ret += "<thead><th>Key</th><th>Value</th></thead>";
			for ( var index in value) {
				ret += "<tr>";
				ret += "<td>" + value[index].key + "</td>";
				ret += "<td>" + value[index].value + "</td>";
				ret += "</tr>";
			}
			ret += "</table>";
		} else {
			ret = value;
		}

		return ret;
	},
};

Handlebars.registerHelper('value-render', function(pv, block) {
	return ValueRenderer.render(pv);
});
