module.exports ={
    html:function(title,list,body,control){
        return `
        <!doctype html>
        <html>
        <head>
        <title>WEB2 - ${title}</title>
        <meta charset="utf-8">
        </head>
        <body>
        <h1><a href="/">WEB2</a></h1>
        <a href="/author">author
        ${list}
        ${control}
        ${body}
        </body>
        </html>
        `;
    },list:function(fileList){
        var list='<ul>'
        var i =0
        while(i<fileList.length){
            list+=`<li><a href="/?id=${fileList[i].id}">${fileList[i].title}</a></li>`
            i++;
        }
        list +='</ul>';
        return list;
    },authorSelect:function(authors,author_id){
        var tag='';
        var i =0;
        
        while(i<authors.length){
            selected=''
            if(author_id === authors[i].id){
                selected = ' selected'
            }
            tag+=`<option value="${authors[i].id}"${selected}>${authors[i].name}</option>`
            i++;
       }
       return `
       <select name="author">
            ${tag}
        </select>
       `
    },authorTable:function(authors){
        var tag ='<table>';
        var i =0;
        while(i<authors.length){
            tag+=`
                <tr>
                    <td>${authors[i].name}</td>
                    <td>${authors[i].profile}</td>
                    <td><a href="/author/update?id=${authors[i].id}">update</a></td>
                    <td>
                        <form action="/author/delete_process" method="post">
                        <input type="hidden" name="id" value="${authors[i].id}">
                        <input type="submit" value="delete"> 
                        </form>
                    </td>
                </tr>
            `
            i++;
        }
        tag+='</table>'
        return tag;
    }
}

