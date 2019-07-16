function pushComments(comments) {
    document.writeln("<br><br>");
    var max = Object.keys(comments).length;
    for (var index = 0; index<max; index++) {
        var json = comments[index];
        var comment = JSON.parse(json);
        document.writeln('<div id="comment">');
        document.writeln("<p'>");
        document.writeln(comment.author);
        document.writeln("<h2>"+comment.title+"</h2>");
        document.writeln(comment.desc+"<br>"+"<br>");
        document.writeln(comment.date);
        document.writeln("</p>");
        document.writeln("</div>");
        if (index + 1 !== max)
            document.writeln("<hr color='#bfa68e'/><br>")
    }
}