function addQuickReplyLocation(messages) {
	messages.push({
	    type: 'quickReplies',
	    content: {
	    	title: "J'ai besoin de votre localisation",
		    buttons: [
		      {
		        type: "location"
		      },
		    ]
	    },
	 });
}