console.log("my script file");

const toggleSidebar = () => {
    if ($('.sidebar').is(":visible")) {
        //true -> then close it
        $(".sidebar").css("display", "none");
        $(".content").css("margin-left", "0%");
    } else {
        //false -> then open it
        $(".sidebar").css("display", "block");
        $(".content").css("margin-left", "20%");
    }
};

const search = () => {
    let query = $("#search-input").val();

    if (query == "") {
        //nothing to do
        $(".search-result").hide();
    } else {
        //console.log(query);


        let url = `http://localhost:8282/search/${query}`;
        fetch(url)
            .then((response) => {
                return response.json();
            })
            .then((data) => {
                console.log(data);
                let text = `<div class="list-group">`;

                data.forEach((contact) => {
                    text += `<a href='/user/${contact.cId}/contact' class="list-group-item list-group-item-action" style="background-color: #f1f9fb;"> ${contact.name} </a>`
                });

                text += `</div>`;


                $(".search-result").html(text);
                $(".search-result").show();
            });

        $(".search-result").show();
    }


};