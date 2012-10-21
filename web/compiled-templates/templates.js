(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
templates['base_paginated_list.hbs'] = template(function (Handlebars,depth0,helpers,partials,data) {
  helpers = helpers || Handlebars.helpers;
  var buffer = "", stack1, self=this, functionType="function", escapeExpression=this.escapeExpression, helperMissing=helpers.helperMissing;

function program1(depth0,data) {
  
  var buffer = "", stack1, foundHelper;
  buffer += "\n<ul class=\"pager\">\n	<li\n		class=\"previous ";
  stack1 = depth0.index_previous_disabled;
  stack1 = helpers['if'].call(depth0, stack1, {hash:{},inverse:self.noop,fn:self.program(2, program2, data)});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\"><a\n		href=\"javascript:paginator.updateList(";
  foundHelper = helpers.index_previous;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.index_previous; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + ")\">&larr; Previous</a></li>\n	<li>";
  foundHelper = helpers.index_start;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.index_start; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + "-";
  foundHelper = helpers.index_end;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.index_end; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + " of ";
  foundHelper = helpers.count;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.count; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + "</li>\n	<li class=\"next ";
  stack1 = depth0.index_next_disabled;
  stack1 = helpers['if'].call(depth0, stack1, {hash:{},inverse:self.noop,fn:self.program(4, program4, data)});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\"><a\n		href=\"javascript:paginator.updateList(";
  foundHelper = helpers.index_next;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.index_next; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + ")\">Next &rarr;</a></li>\n</ul>\n";
  foundHelper = helpers.block;
  stack1 = foundHelper ? foundHelper.call(depth0, "item_list", {hash:{},inverse:self.noop,fn:self.program(6, program6, data)}) : helperMissing.call(depth0, "block", "item_list", {hash:{},inverse:self.noop,fn:self.program(6, program6, data)});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\n<ul class=\"pager\">\n	<li\n		class=\"previous ";
  stack1 = depth0.index_previous_disabled;
  stack1 = helpers['if'].call(depth0, stack1, {hash:{},inverse:self.noop,fn:self.program(8, program8, data)});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\"><a\n		href=\"javascript:paginator.updateList(";
  foundHelper = helpers.index_previous;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.index_previous; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + ")\">&larr; Previous</a></li>\n	<li>";
  foundHelper = helpers.index_start;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.index_start; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + "-";
  foundHelper = helpers.index_end;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.index_end; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + " of ";
  foundHelper = helpers.count;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.count; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + "</li>\n	<li class=\"next ";
  stack1 = depth0.index_next_disabled;
  stack1 = helpers['if'].call(depth0, stack1, {hash:{},inverse:self.noop,fn:self.program(10, program10, data)});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\"><a\n		href=\"javascript:paginator.updateList(";
  foundHelper = helpers.index_next;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.index_next; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + ")\">Next &rarr;</a></li>\n</ul>\n";
  return buffer;}
function program2(depth0,data) {
  
  
  return "disabled";}

function program4(depth0,data) {
  
  
  return "disabled";}

function program6(depth0,data) {
  
  
  return "\n";}

function program8(depth0,data) {
  
  
  return "disabled";}

function program10(depth0,data) {
  
  
  return "disabled";}

function program12(depth0,data) {
  
  var buffer = "", stack1, foundHelper;
  buffer += "\n<div class=\"alert alert-warning\">\n	<b>Empty!</b> ";
  foundHelper = helpers.block;
  stack1 = foundHelper ? foundHelper.call(depth0, "empty_message", {hash:{},inverse:self.noop,fn:self.program(13, program13, data)}) : helperMissing.call(depth0, "block", "empty_message", {hash:{},inverse:self.noop,fn:self.program(13, program13, data)});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += ".\n</div>\n";
  return buffer;}
function program13(depth0,data) {
  
  var buffer = "";
  return buffer;}

  stack1 = depth0.items;
  stack1 = helpers['if'].call(depth0, stack1, {hash:{},inverse:self.noop,fn:self.program(1, program1, data)});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\n";
  stack1 = depth0.items;
  stack1 = helpers.unless.call(depth0, stack1, {hash:{},inverse:self.noop,fn:self.program(12, program12, data)});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\n";
  return buffer;});
templates['entity_list.hbs'] = template(function (Handlebars,depth0,helpers,partials,data) {
  helpers = helpers || Handlebars.helpers; partials = partials || Handlebars.partials;
  var buffer = "", stack1, foundHelper, functionType="function", escapeExpression=this.escapeExpression, self=this, blockHelperMissing=helpers.blockHelperMissing, helperMissing=helpers.helperMissing;

function program1(depth0,data) {
  
  var buffer = "", stack1, foundHelper;
  buffer += "\n<table class=\"table table-hover table-striped\">\n	<thead>\n		<tr>\n			<th>Name</th>\n			<th style=\"width: 10%\">Action</th>\n		</tr>\n	</thead>\n	<tbody>\n		";
  foundHelper = helpers.items;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{},inverse:self.noop,fn:self.programWithDepth(program2, data, depth0)}); }
  else { stack1 = depth0.items; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  if (!helpers.items) { stack1 = blockHelperMissing.call(depth0, stack1, {hash:{},inverse:self.noop,fn:self.programWithDepth(program2, data, depth0)}); }
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\n	</tbody>\n</table>\n";
  return buffer;}
function program2(depth0,data,depth1) {
  
  var buffer = "", stack1, foundHelper;
  buffer += "\n		<tr>\n			<td>";
  foundHelper = helpers.name;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.name; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + "</td>\n			<td><a class=\"btn btn-small btn-link\"\n				href=\"";
  stack1 = depth1.mustacheletPath;
  stack1 = typeof stack1 === functionType ? stack1() : stack1;
  buffer += escapeExpression(stack1) + "/browse/entity/";
  stack1 = depth0.type;
  stack1 = stack1 == null || stack1 === false ? stack1 : stack1.name;
  stack1 = typeof stack1 === functionType ? stack1() : stack1;
  buffer += escapeExpression(stack1) + "/";
  foundHelper = helpers.name;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.name; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + "\"><i class=\" icon-th-list\"></i></a></td>\n		</tr>\n		";
  return buffer;}

function program4(depth0,data) {
  
  
  return "There are no defined entities.";}

  foundHelper = helpers.partial;
  stack1 = foundHelper ? foundHelper.call(depth0, "item_list", {hash:{},inverse:self.noop,fn:self.program(1, program1, data)}) : helperMissing.call(depth0, "partial", "item_list", {hash:{},inverse:self.noop,fn:self.program(1, program1, data)});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\n";
  foundHelper = helpers.partial;
  stack1 = foundHelper ? foundHelper.call(depth0, "empty_message", {hash:{},inverse:self.noop,fn:self.program(4, program4, data)}) : helperMissing.call(depth0, "partial", "empty_message", {hash:{},inverse:self.noop,fn:self.program(4, program4, data)});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\n";
  stack1 = depth0;
  stack1 = self.invokePartial(partials.base_paginated_list, 'base_paginated_list', stack1, helpers, partials);;
  if(stack1 || stack1 === 0) { buffer += stack1; }
  return buffer;});
templates['event_list.hbs'] = template(function (Handlebars,depth0,helpers,partials,data) {
  helpers = helpers || Handlebars.helpers; partials = partials || Handlebars.partials;
  var buffer = "", stack1, foundHelper, self=this, functionType="function", escapeExpression=this.escapeExpression, helperMissing=helpers.helperMissing, blockHelperMissing=helpers.blockHelperMissing;

function program1(depth0,data) {
  
  var buffer = "", stack1, foundHelper;
  buffer += "\n<table class=\"table table-hover table-striped\">\n	<thead>\n		<tr>\n			<th>&nbsp;</th>\n			<th>Date</th>\n			<th>Event</th>\n			<th>Details</th>\n		</tr>\n	</thead>\n	<tbody>\n		";
  foundHelper = helpers.items;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{},inverse:self.noop,fn:self.program(2, program2, data)}); }
  else { stack1 = depth0.items; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  if (!helpers.items) { stack1 = blockHelperMissing.call(depth0, stack1, {hash:{},inverse:self.noop,fn:self.program(2, program2, data)}); }
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\n	</tbody>\n</table>\n";
  return buffer;}
function program2(depth0,data) {
  
  var buffer = "", stack1, foundHelper;
  buffer += "\n		<tr class=\"";
  stack1 = depth0.successful;
  stack1 = helpers['if'].call(depth0, stack1, {hash:{},inverse:self.noop,fn:self.program(3, program3, data)});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  stack1 = depth0.successful;
  stack1 = helpers.unless.call(depth0, stack1, {hash:{},inverse:self.noop,fn:self.program(5, program5, data)});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\">\n			<td>";
  foundHelper = helpers.type;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.type; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + "</td>\n			<td style=\"white-space: nowrap;\">";
  stack1 = depth0.timestamp;
  foundHelper = helpers.dateFormat;
  stack1 = foundHelper ? foundHelper.call(depth0, stack1, {hash:{}}) : helperMissing.call(depth0, "dateFormat", stack1, {hash:{}});
  buffer += escapeExpression(stack1) + "</td>\n			<td>";
  foundHelper = helpers.message;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.message; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + "</td>\n			<td>";
  foundHelper = helpers.reason;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.reason; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + "</td>\n		</tr>\n		";
  return buffer;}
function program3(depth0,data) {
  
  
  return "success";}

function program5(depth0,data) {
  
  
  return "error";}

function program7(depth0,data) {
  
  
  return "No source adaptor events defined.";}

  foundHelper = helpers.partial;
  stack1 = foundHelper ? foundHelper.call(depth0, "item_list", {hash:{},inverse:self.noop,fn:self.program(1, program1, data)}) : helperMissing.call(depth0, "partial", "item_list", {hash:{},inverse:self.noop,fn:self.program(1, program1, data)});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\n";
  foundHelper = helpers.partial;
  stack1 = foundHelper ? foundHelper.call(depth0, "empty_message", {hash:{},inverse:self.noop,fn:self.program(7, program7, data)}) : helperMissing.call(depth0, "partial", "empty_message", {hash:{},inverse:self.noop,fn:self.program(7, program7, data)});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\n";
  stack1 = depth0;
  stack1 = self.invokePartial(partials.base_paginated_list, 'base_paginated_list', stack1, helpers, partials);;
  if(stack1 || stack1 === 0) { buffer += stack1; }
  return buffer;});
templates['paginated_list_base.hbs'] = template(function (Handlebars,depth0,helpers,partials,data) {
  helpers = helpers || Handlebars.helpers;
  var buffer = "", stack1, self=this, functionType="function", escapeExpression=this.escapeExpression, helperMissing=helpers.helperMissing;

function program1(depth0,data) {
  
  var buffer = "", stack1, foundHelper;
  buffer += "\n<ul class=\"pager\">\n	<li\n		class=\"previous ";
  stack1 = depth0.index_previous_disabled;
  stack1 = helpers['if'].call(depth0, stack1, {hash:{},inverse:self.noop,fn:self.program(2, program2, data)});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\"><a\n		href=\"javascript:paginator.updateList(";
  foundHelper = helpers.index_previous;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.index_previous; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + ")\">&larr; Previous</a></li>\n	<li>";
  foundHelper = helpers.index_start;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.index_start; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + "-";
  foundHelper = helpers.index_end;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.index_end; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + " of ";
  foundHelper = helpers.count;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.count; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + "</li>\n	<li class=\"next ";
  stack1 = depth0.index_next_disabled;
  stack1 = helpers['if'].call(depth0, stack1, {hash:{},inverse:self.noop,fn:self.program(4, program4, data)});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\"><a\n		href=\"javascript:paginator.updateList(";
  foundHelper = helpers.index_next;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.index_next; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + ")\">Next &rarr;</a></li>\n</ul>\n";
  foundHelper = helpers.block;
  stack1 = foundHelper ? foundHelper.call(depth0, "item_list", {hash:{},inverse:self.noop,fn:self.program(6, program6, data)}) : helperMissing.call(depth0, "block", "item_list", {hash:{},inverse:self.noop,fn:self.program(6, program6, data)});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\n<ul class=\"pager\">\n	<li\n		class=\"previous ";
  stack1 = depth0.index_previous_disabled;
  stack1 = helpers['if'].call(depth0, stack1, {hash:{},inverse:self.noop,fn:self.program(8, program8, data)});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\"><a\n		href=\"javascript:paginator.updateList(";
  foundHelper = helpers.index_previous;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.index_previous; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + ")\">&larr; Previous</a></li>\n	<li>";
  foundHelper = helpers.index_start;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.index_start; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + "-";
  foundHelper = helpers.index_end;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.index_end; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + " of ";
  foundHelper = helpers.count;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.count; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + "</li>\n	<li class=\"next ";
  stack1 = depth0.index_next_disabled;
  stack1 = helpers['if'].call(depth0, stack1, {hash:{},inverse:self.noop,fn:self.program(10, program10, data)});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\"><a\n		href=\"javascript:paginator.updateList(";
  foundHelper = helpers.index_next;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.index_next; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + ")\">Next &rarr;</a></li>\n</ul>\n";
  return buffer;}
function program2(depth0,data) {
  
  
  return "disabled";}

function program4(depth0,data) {
  
  
  return "disabled";}

function program6(depth0,data) {
  
  
  return "\n";}

function program8(depth0,data) {
  
  
  return "disabled";}

function program10(depth0,data) {
  
  
  return "disabled";}

function program12(depth0,data) {
  
  var buffer = "", stack1, foundHelper;
  buffer += "\n<div class=\"alert alert-warning\">\n	<b>Empty!</b> ";
  foundHelper = helpers.block;
  stack1 = foundHelper ? foundHelper.call(depth0, "empty_message", {hash:{},inverse:self.noop,fn:self.program(13, program13, data)}) : helperMissing.call(depth0, "block", "empty_message", {hash:{},inverse:self.noop,fn:self.program(13, program13, data)});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += ".\n</div>\n";
  return buffer;}
function program13(depth0,data) {
  
  var buffer = "";
  return buffer;}

  stack1 = depth0.items;
  stack1 = helpers['if'].call(depth0, stack1, {hash:{},inverse:self.noop,fn:self.program(1, program1, data)});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\n";
  stack1 = depth0.items;
  stack1 = helpers.unless.call(depth0, stack1, {hash:{},inverse:self.noop,fn:self.program(12, program12, data)});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\n";
  return buffer;});
templates['plugin_configuration.hbs'] = template(function (Handlebars,depth0,helpers,partials,data) {
  helpers = helpers || Handlebars.helpers;
  var stack1, foundHelper, functionType="function", escapeExpression=this.escapeExpression, self=this, blockHelperMissing=helpers.blockHelperMissing;

function program1(depth0,data) {
  
  var buffer = "", stack1, foundHelper;
  buffer += "\n<label>";
  foundHelper = helpers.description;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.description; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1);
  foundHelper = helpers.required;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{},inverse:self.noop,fn:self.program(2, program2, data)}); }
  else { stack1 = depth0.required; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  if (!helpers.required) { stack1 = blockHelperMissing.call(depth0, stack1, {hash:{},inverse:self.noop,fn:self.program(2, program2, data)}); }
  if(stack1 || stack1 === 0) { buffer += stack1; }
  foundHelper = helpers.hidden;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{},inverse:self.noop,fn:self.program(4, program4, data)}); }
  else { stack1 = depth0.hidden; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  if (!helpers.hidden) { stack1 = blockHelperMissing.call(depth0, stack1, {hash:{},inverse:self.noop,fn:self.program(4, program4, data)}); }
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "</label>\n<input type=\"text\" value=\"";
  foundHelper = helpers.value;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.value; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + "\" name=\"config.";
  foundHelper = helpers.key;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.key; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + "\">\n";
  return buffer;}
function program2(depth0,data) {
  
  
  return " (required)";}

function program4(depth0,data) {
  
  
  return " (hidden)";}

  foundHelper = helpers.parameters;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{},inverse:self.noop,fn:self.program(1, program1, data)}); }
  else { stack1 = depth0.parameters; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  if (!helpers.parameters) { stack1 = blockHelperMissing.call(depth0, stack1, {hash:{},inverse:self.noop,fn:self.program(1, program1, data)}); }
  if(stack1 || stack1 === 0) { return stack1; }
  else { return ''; }});
templates['type_list.hbs'] = template(function (Handlebars,depth0,helpers,partials,data) {
  helpers = helpers || Handlebars.helpers; partials = partials || Handlebars.partials;
  var buffer = "", stack1, foundHelper, functionType="function", escapeExpression=this.escapeExpression, self=this, blockHelperMissing=helpers.blockHelperMissing, helperMissing=helpers.helperMissing;

function program1(depth0,data) {
  
  var buffer = "", stack1, foundHelper;
  buffer += "\n<table class=\"table table-hover table-striped\">\n	<thead>\n		<tr>\n			<th>Name</th>\n			<th>Description</th>\n			<th style=\"width: 10%\">Action</th>\n		</tr>\n	</thead>\n	<tbody>\n		";
  foundHelper = helpers.items;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{},inverse:self.noop,fn:self.programWithDepth(program2, data, depth0)}); }
  else { stack1 = depth0.items; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  if (!helpers.items) { stack1 = blockHelperMissing.call(depth0, stack1, {hash:{},inverse:self.noop,fn:self.programWithDepth(program2, data, depth0)}); }
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\n	</tbody>\n</table>\n";
  return buffer;}
function program2(depth0,data,depth1) {
  
  var buffer = "", stack1, foundHelper;
  buffer += "\n		<tr>\n			<td>";
  foundHelper = helpers.name;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.name; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + "</td>\n			<td>";
  foundHelper = helpers.description;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.description; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + "</td>\n			<td><a class=\"btn btn-small btn-link\"\n				href=\"";
  stack1 = depth1.mustacheletPath;
  stack1 = typeof stack1 === functionType ? stack1() : stack1;
  buffer += escapeExpression(stack1) + "/browse/type/";
  foundHelper = helpers.name;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.name; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + "\"><i class=\" icon-th-list\"></i></a></td>\n		</tr>\n		";
  return buffer;}

function program4(depth0,data) {
  
  
  return "There are no defined categories.";}

  foundHelper = helpers.partial;
  stack1 = foundHelper ? foundHelper.call(depth0, "item_list", {hash:{},inverse:self.noop,fn:self.program(1, program1, data)}) : helperMissing.call(depth0, "partial", "item_list", {hash:{},inverse:self.noop,fn:self.program(1, program1, data)});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += " ";
  foundHelper = helpers.partial;
  stack1 = foundHelper ? foundHelper.call(depth0, "empty_message", {hash:{},inverse:self.noop,fn:self.program(4, program4, data)}) : helperMissing.call(depth0, "partial", "empty_message", {hash:{},inverse:self.noop,fn:self.program(4, program4, data)});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\n";
  stack1 = depth0;
  stack1 = self.invokePartial(partials.base_paginated_list, 'base_paginated_list', stack1, helpers, partials);;
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\n";
  return buffer;});
templates['version_measurement_list.hbs'] = template(function (Handlebars,depth0,helpers,partials,data) {
  helpers = helpers || Handlebars.helpers; partials = partials || Handlebars.partials;
  var buffer = "", stack1, foundHelper, functionType="function", escapeExpression=this.escapeExpression, self=this, helperMissing=helpers.helperMissing;

function program1(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\n<table class=\"table table-hover table-striped\">\n	<thead>\n		<tr>\n			<th>Date</th>\n			<th>Source</th>\n			<th>Adaptor</th>\n		</tr>\n	</thead>\n	<tbody>\n		";
  stack1 = depth0.items;
  stack1 = helpers.each.call(depth0, stack1, {hash:{},inverse:self.noop,fn:self.programWithDepth(program2, data, depth0)});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\n	</tbody>\n</table>\n";
  return buffer;}
function program2(depth0,data,depth1) {
  
  var buffer = "", stack1, foundHelper;
  buffer += "\n		<tr>\n			<td style=\"white-space: nowrap;\">";
  stack1 = depth0.timestamp;
  foundHelper = helpers.dateFormat;
  stack1 = foundHelper ? foundHelper.call(depth0, stack1, {hash:{}}) : helperMissing.call(depth0, "dateFormat", stack1, {hash:{}});
  buffer += escapeExpression(stack1) + "</td>\n			<td>";
  stack1 = depth0.adaptor;
  stack1 = stack1 == null || stack1 === false ? stack1 : stack1.source;
  stack1 = helpers['with'].call(depth0, stack1, {hash:{},inverse:self.noop,fn:self.programWithDepth(program3, data, depth1)});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "</td>\n			<td>";
  stack1 = depth0.adaptor;
  stack1 = helpers['with'].call(depth0, stack1, {hash:{},inverse:self.noop,fn:self.programWithDepth(program6, data, depth1)});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "</td>\n		</tr>\n		";
  return buffer;}
function program3(depth0,data,depth2) {
  
  var buffer = "", stack1, foundHelper;
  buffer += "<a href=\"";
  stack1 = depth2.mustacheletPath;
  stack1 = typeof stack1 === functionType ? stack1() : stack1;
  buffer += escapeExpression(stack1) + "/browse/source/";
  foundHelper = helpers.name;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.name; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + "\">";
  foundHelper = helpers.name;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.name; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1);
  stack1 = depth0.description;
  stack1 = helpers['if'].call(depth0, stack1, {hash:{},inverse:self.noop,fn:self.program(4, program4, data)});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "</a>";
  return buffer;}
function program4(depth0,data) {
  
  var buffer = "", stack1, foundHelper;
  buffer += ": ";
  foundHelper = helpers.description;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.description; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1);
  return buffer;}

function program6(depth0,data,depth2) {
  
  var buffer = "", stack1, foundHelper;
  buffer += "<a href=\"";
  stack1 = depth2.mustacheletPath;
  stack1 = typeof stack1 === functionType ? stack1() : stack1;
  buffer += escapeExpression(stack1) + "/browse/adaptor/";
  foundHelper = helpers.instance;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.instance; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + "\">";
  foundHelper = helpers.instance;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.instance; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + " (";
  foundHelper = helpers.name;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.name; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + "-v";
  foundHelper = helpers.version;
  if (foundHelper) { stack1 = foundHelper.call(depth0, {hash:{}}); }
  else { stack1 = depth0.version; stack1 = typeof stack1 === functionType ? stack1() : stack1; }
  buffer += escapeExpression(stack1) + ")</a>";
  return buffer;}

function program8(depth0,data) {
  
  
  return "No measurements found.";}

  foundHelper = helpers.partial;
  stack1 = foundHelper ? foundHelper.call(depth0, "item_list", {hash:{},inverse:self.noop,fn:self.program(1, program1, data)}) : helperMissing.call(depth0, "partial", "item_list", {hash:{},inverse:self.noop,fn:self.program(1, program1, data)});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\n";
  foundHelper = helpers.partial;
  stack1 = foundHelper ? foundHelper.call(depth0, "empty_message", {hash:{},inverse:self.noop,fn:self.program(8, program8, data)}) : helperMissing.call(depth0, "partial", "empty_message", {hash:{},inverse:self.noop,fn:self.program(8, program8, data)});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\n";
  stack1 = depth0;
  stack1 = self.invokePartial(partials.base_paginated_list, 'base_paginated_list', stack1, helpers, partials);;
  if(stack1 || stack1 === 0) { buffer += stack1; }
  return buffer;});
})();