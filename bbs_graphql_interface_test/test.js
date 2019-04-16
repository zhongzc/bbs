
const fire_all_tests = async () => {
	await fire_unit_tests();
	// await fire_behav_tests();
}

const remoteAddress = "http://localhost:4000";

const sendGQL = (queryPayloadObject) => {
	return fetch(remoteAddress + "/graphql/", {
		method: "POST",
		body: JSON.stringify({
			variables: queryPayloadObject.variables,
			query: queryPayloadObject.query
		}),
		headers: {
			'Content-Type': 'application/json',
			'Authorization': "Bearer " + queryPayloadObject.auth || "not-set",
		}
	}).then(response => {
		if (response.ok) {
			return response.json();
		} else {
			throw new Error("--Network Error--");
		}
	}).then(json => {
		// dig into two layer 
		for (let data in json) {
			for (let f in json[data]) {
				return json[data][f];
			}
		}
	});
};

// =======================================useful func=====================================
// =======================================useful func=====================================
// =======================================useful func=====================================
const CURRENT_MAX_ID = "currentMaxId";

let suffix = localStorage.getItem(CURRENT_MAX_ID) || 0;
const generateEmailPassAndNickname = () => {
	const newSuffix = ++suffix;
	localStorage.setItem(CURRENT_MAX_ID, newSuffix)
	return ["tangenta" + newSuffix + "@126.com", "pass1234", "tangenta" + newSuffix];
};

// ---------convenient func---------

// [dependencies: signUp]
const after_signUp = (func) => {
	[username, password, nickname] = generateEmailPassAndNickname();
	return signUp(username, password, nickname)
		.then(signUpResult => {
			const auth = signUpResult.token;
			return func(auth, username, password, nickname);
		});
};

// [dependencies: signUp, loggedId]
const provided_userId = (func) =>
	after_signUp((auth, username, password, nickname) =>
		loggedId(auth).then(result =>
			func(result.userid, username, password, nickname)
		)
	);

// [dependencies: signUp, publishFound]
const after_publishFound = (func) =>
	after_signUp(auth => {
		const testObj = {
			itemName: "qwert",
			description: "qwerty",
			position: "qwertyu",
			contact: "1121234567",
			imageBase64: "aGVsbG93b3JsZCE=",
			time: Date.now(),
		};
		return publishFound(testObj, auth).then(result =>
			func(auth, result.id, testObj)
		)
	});

// [dependencies: signUp, publishLost]
const after_publishLost = (func) =>
	after_signUp(auth => {
		const testObj = {
			itemName: "qwert",
			description: "qwerty",
			position: "qwertyu",
			contact: "1121234567",
			imageBase64: "aGVsbG93b3JsZCE=",
			time: Date.now(),
		};
		return publishLost(testObj, auth).then(result =>
			func(auth, result.id, testObj)
		)
	});

// [dependencies: signUp, publishLost | publishFound]
const after_n_lost_or_found_publish = (foundOrLostStr, nTimes, func) => {
	let isFound = false;
	if (foundOrLostStr === "lost") isFound = false;
	else if (foundOrLostStr === "found") isFound = true;
	else throw new Error("not found or lost");

	return after_signUp(auth => {
		let chain = Promise.resolve();
		let pubItems = [];
		let pubItemIds = [];
		for (let i = 0; i < nTimes; ++i) {
			const item = {
				itemName: "qwert" + i,
				description: "qwerty" + i,
				position: "qwertyu" + i,
				contact: "1121234567" + i,
				imageBase64: "aGVsbG93b3JsZCE=",
				time: Date.now(),
			};
			pubItems.push(item);
			chain = chain.then(() => isFound ? publishFound(item, auth) : publishLost(item, auth)
				.then(result => pubItemIds.push(result.itemId)));
		}

		return chain.then(() => func(auth, pubItems, pubItemIds));
	})
};

// [dependencies: signUp, createPosts, deletePosts]
const after_n_post_create = (nTimes, func) => {
	return after_signUp(auth => {
		let chain = Promise.resolve();
		let pubPosts = [];
		let pubPostIds = [];
		for (let i = 0; i < nTimes; ++i) {
			const post = {
				title: "title" + i,
				content: "content" + i
			}
			pubPosts.push(post);
			chain = chain.then(() => createPost(post, auth))
				.then(result => pubPostIds.push(result.postId));
		}

		return chain.then(() => func(auth, pubPosts, pubPostIds));
	});
}

//---------unit test---------

let unitTests = [];
const unit_test = (name, f) => {
	unitTests.push({ name: name, func: f });
};
const unit_test_only = (name, f) => {
	unitTests.push({ name: name, func: f, unique: true });
}
const fire_unit_tests = async () => {
	await fire_tests("unit test", unitTests);
}

//---------behavior test---------

let behavTests = [];
const behav_test = (name, f) => {
	behavTests.push({ name: name, func: f });
};
const behav_test_only = (name, f) => {
	behavTests.push({ name: name, func: f, unique: true });
}
const fire_behav_tests = async () => {
	await fire_tests("behavior test", behavTests);
}

//---------test firing---------

const fire_tests = async (kind, tests) => {
	const uniqueTest = tests.filter(testObj => testObj.unique !== undefined);
	if (uniqueTest.length > 1) {
		console.error("===== more than one unique " + kind + "s are found: (" + uniqueTest.map(x => x.name).join(", ") + ") =====");
		return;
	}
	const testsToRun = uniqueTest.length === 0 ? tests : uniqueTest;
	let passTestCounter = 0;
	for (let f of testsToRun) {
		await f.func()
			.then(() => {
				passTestCounter++;
				return console.log("SUCCESS # " + kind + ": " + f.name)
			})
			.then(() => new Promise(resolve => setTimeout(resolve, 10)))  // visual enjoyment
			.then(() => cleanUpRoutine())
			.then(() => new Promise(resolve => setTimeout(resolve, 10)))
			.catch(error => {
				console.error("=== (" + f.name + ") " + kind + " FAILED ===");
				console.error(error);
			});
	}
	console.log(kind + " pass/run: " + passTestCounter + "/" + testsToRun.length);
}

const cleanUpRoutine = () => {
	const resetLostFound = `
		mutation {
			resetLostFound
		}
	`;
	sendGQL({
		query: resetLostFound,
	})
	const resetPost = `
		mutation {
			resetPost
		}
	`;
	sendGQL({
		query: resetPost,
	})
}

// ---------assertions---------

const assert = (bool) => {
	if (!bool) throw new Error("assertion failed");
};

const assertEq = (left, right) => {
	if (left !== right) {
		throw new Error("left: " + left + " is not equal to right: " + right);
	}
}
	;
const assertNotEq = (left, right) => {
	if (left === right) {
		throw new Error("left: " + left + " is equal to right: " + right);
	}
}

const assertNonEmpty = (obj) => {
	if (obj === undefined || obj === null) {
		throw new Error("empty value: " + obj);
	}
}

const fail = (errMsg) => {
	throw new Error("test failed: " + errMsg);
};

// =========================================schema========================================
// =========================================schema========================================
// =========================================schema========================================

// ========================================mutation=======================================

const SIGN_UP = `
	mutation SignUp($username: String!, $password: String!, $nickname: String!) {
		signUp(username: $username, password: $password, nickname: $nickname) {
			... on SignUpError {
				error
			}
			... on SignUpPayload {
				token
			}
		}
	}
`;

const signUp = (username, password, nickname) => {
	return sendGQL({
		query: SIGN_UP,
		variables: {
			username: username,
			password: password,
			nickname: nickname,
		},
		auth: "not-logged-in",
	});
};

unit_test("signUp", () =>
	after_signUp(auth =>
		assert(auth.length !== 0)
	)
);


// =============================================

const LOG_IN = `
	mutation LogIn($username: String!, $password: String!) {
		logIn(username: $username, password: $password) {
			... on LogInError {
				error
			}
			... on LogInPayload {
				token
			}
		}
	}
`;
const logIn = (username, password) => {
	return sendGQL({
		query: LOG_IN,
		variables: {
			username: username,
			password: password,
		},
	});
};

unit_test("login", () =>
	after_signUp((auth, uname, pass) =>
		logIn(uname, pass).then(data => {
			assert(data.error === undefined);
			assert(data.token.length !== 0);
		})
	)
);


// =============================================

const LOG_OUT = `
	mutation LogOut {
		logOut {
			error
		}
	}
`;
const logOut = (auth) => {
	return sendGQL({
		query: LOG_OUT,
		auth: auth,
	});
};
unit_test("logout", () =>
	after_signUp(auth =>
		logOut(auth).then(error => {
			assert(error === null);
		})
	)
);

// =============================================
const CONFIRM_PASSWORD = `
	mutation ConfirmPassword($username: String!, $password: String!) {
		confirmPassword(username: $username, password: $password) {
			... on ConfirmPasswordError {
				error
			}
			... on ConfirmPasswordPayload {
				resetToken
			}
		}
	}
`;
const confirmPassword = (username, password, auth) => {
	return sendGQL({
		query: CONFIRM_PASSWORD,
		variables: {
			username: username,
			password: password,
		},
		auth: auth
	})
}
unit_test("confirmPassword - correct password", () =>
	after_signUp((auth, uname, pass) =>
		confirmPassword(uname, pass, auth).then(result => {
			assert(result.error === undefined);
		})
	)
);

unit_test("confirmPassword - wrong password", () =>
	after_signUp((auth, uname, pass) =>
		confirmPassword(uname, pass + "fail it", auth).then(result => {
			assert(result.resetToken === undefined);
		})
	)
);

// =============================================

const CHANGE_PASSWORD = `
	mutation ChangePassword($resetToken: String!, $newPassword: String!) {
		changePassword(resetToken: $resetToken, newPassword: $newPassword) {
			error
		}
	}
`;

const changePassword = (resetToken, newPassword, userToken) => {
	return sendGQL({
		query: CHANGE_PASSWORD,
		variables: {
			resetToken: resetToken,
			newPassword: newPassword,
		},
		auth: userToken
	});
};

unit_test("changePassword", () =>
	after_signUp((auth, uname, pass) =>
		confirmPassword(uname, pass, auth).then(data => {
			const resetToken = data.resetToken;
			const newPassword = "new" + pass;
			return changePassword(resetToken, newPassword, auth).then(error => {
				assert(error === null);
				logIn(uname, password).then(loginResult => {
					assert(loginResult.token === undefined);
				})
				logIn(uname, newPassword).then(loginResult => {
					assert(loginResult.error === undefined);
				})
			});
		})
	)
);


// =============================================

const UPLOAD_USER_PROFILE = `
	mutation UploadUserProfile($base64Image: String!) {
		uploadUserProfile(base64Image: $base64Image) {
			... on ModifyPersonInfoSuccess {
				personalInfo {
					userId
				}
			}
			... on ModifyPersonInfoError {
				error
			}
		}
	}
`;

const uploadUserProfile = (base64Image, userToken) => {
	return sendGQL({
		query: UPLOAD_USER_PROFILE,
		variables: {
			base64Image: base64Image
		},
		auth: userToken
	});
}

// user uploaded image
function encodeImageFileAsURL(element, onLoaded) {
	var file = element.files[0];
	var reader = new FileReader();
	reader.onloadend = function () {
		const base64Img = reader.result.replace(/^data:image\/(png|jpg);base64,/, "");
		onLoaded(base64Img);
	}
	reader.readAsDataURL(file);
}

// image from internet
function getBase64Image(img) {
	var canvas = document.createElement("canvas");
	canvas.width = img.width;
	canvas.height = img.height;
	var ctx = canvas.getContext("2d");
	ctx.drawImage(img, 0, 0);
	var dataURL = canvas.toDataURL("image/png");
	return dataURL.replace(/^data:image\/(png|jpg);base64,/, "");
}

unit_test("upload user profile", () =>
	after_signUp(auth => {
		const base64Image = getBase64Image(document.getElementById("test-img"));
		return uploadUserProfile(base64Image, auth).then(result => {
			assertNonEmpty(result.personalInfo.userId);
		});
	})
);

// =============================================

const CHANGE_GENDER = `
	mutation ChangeGender($gender: String!) {
		changeGender(gender: $gender) {
			... on ModifyPersonInfoSuccess {
				personalInfo {
					userId
				}
			}
			... on ModifyPersonInfoError {
				error
			}
		}
	}
`;

const changeGender = (gender, userToken) => {
	return sendGQL({
		query: CHANGE_GENDER,
		variables: {
			gender: gender,
		},
		auth: userToken
	});
};

unit_test("change gender - invalid gender", () =>
	after_signUp(auth => {
		changeGender("unknown-gender", auth).then(result => {
			assertNotEq(result.error.length, 0)
		})
	})
);
unit_test("change gender - valid gender", () =>
	after_signUp(auth => {
		changeGender("male", auth).then(result => {
			assertNonEmpty(result.personalInfo.userId);
		})
	})
);
// =============================================

const CHANGE_GRADE = `
	mutation ChangeGrade($grade: String!) {
		changeGrade(grade: $grade) {
			... on ModifyPersonInfoSuccess {
				personalInfo {
					userId
				}
			}
			... on ModifyPersonInfoError {
				error
			}
		}
}
`;

const changeGrade = (grade, userToken) => {
	return sendGQL({
		query: CHANGE_GRADE,
		variables: {
			grade: grade,
		},
		auth: userToken
	});
};

unit_test("change grade", () =>
	after_signUp(auth => {
		changeGrade("2017级", auth).then(result => {
			assert(result.personalInfo.userId);
		})
	})
);
// =============================================

const CHANGE_INTRODUCTION = `
	mutation ChangeIntroduction($introduction: String!) {
		changeIntroduction(introduction: $introduction) {
			... on ModifyPersonInfoSuccess {
				personalInfo {
					userId
				}
			}
			... on ModifyPersonInfoError {
				error
			}
		}
}
`;

const changeIntroduction = (introduction, userToken) => {
	return sendGQL({
		query: CHANGE_INTRODUCTION,
		variables: {
			introduction: introduction,
		},
		auth: userToken
	});
};

unit_test("change introduction", () =>
	after_signUp(auth => {
		changeIntroduction("个人介绍", auth).then(result => {
			assertNonEmpty(result.personalInfo.userId);
		})
	})
);
// =============================================

const CHANGE_NICKNAME = `
	mutation ChangeNickname($nickname: String!) {
		changeNickname(nickname: $nickname) {
			... on ModifyPersonInfoSuccess {
				personalInfo {
					userId
				}
			}
			... on ModifyPersonInfoError {
				error
			}
		}
}
`;

const changeNickname = (nickname, userToken) => {
	return sendGQL({
		query: CHANGE_NICKNAME,
		variables: {
			nickname: nickname,
		},
		auth: userToken
	});
};

unit_test("change nickname", () =>
	after_signUp(auth => {
		changeNickname("昵称", auth).then(result => {
			assertNonEmpty(result.personalInfo.userId);
		})
	})
);
// =============================================

const CHANGE_ACADEMY = `
	mutation ChangeAcademy($academy: String!) {
		changeAcademy(academy: $academy) {
			... on ModifyPersonInfoSuccess {
				personalInfo {
					userId
				}
			}
			... on ModifyPersonInfoError {
				error
			}
		}
}
`;

const changeAcademy = (academy, userToken) => {
	return sendGQL({
		query: CHANGE_ACADEMY,
		variables: {
			academy: academy,
		},
		auth: userToken
	});
};

unit_test("change academy - invalid academy", () =>
	after_signUp(auth => {
		changeAcademy("学院", auth).then(result => {
			assertNotEq(result.error, undefined);
		})
	})
);
unit_test("change academy - valid academy", () =>
	after_signUp(auth => {
		changeAcademy("计算机科学与工程学院", auth).then(result => {
			assertNonEmpty(result.personalInfo.userId);
		})
	})
);
// =============================================

const CHANGE_MAJOR = `
	mutation ChangeMajor($major: String!) {
		changeMajor(major: $major) {
			... on ModifyPersonInfoSuccess {
				personalInfo {
					userId
				}
			}
			... on ModifyPersonInfoError {
				error
			}
		}
	}
`;

const changeMajor = (major, userToken) => {
	return sendGQL({
		query: CHANGE_MAJOR,
		variables: {
			major: major,
		},
		auth: userToken
	});
};

unit_test("change major - invalid major", () =>
	after_signUp(auth => {
		changeMajor("专业", auth).then(result => {
			assertNonEmpty(result.error);
		})
	})
);
unit_test("change major - valid major", () =>
	after_signUp(auth => {
		changeMajor("网络工程", auth).then(result => {
			assertNonEmpty(result.personalInfo.userId);
		})
	})
);
// =============================================

const PUBLISH_FOUND = `
	mutation PublishFound($itemInfo: ItemInfoInput!) {
		publishFound(itemInfo: $itemInfo) {
			... on FoundItemInfo {
				id
			}
			... on LostFoundError {
				error
			}
		}
	}
`;

const publishFound = (itemInfo, userToken) => sendGQL({
	query: PUBLISH_FOUND,
	variables: {
		itemInfo: itemInfo,
	},
	auth: userToken
});

unit_test("publish found", () =>
	after_signUp(auth =>
		publishFound({
			itemName: "boyfriend",
			description: "pretty",
			position: "your eyes",
			contact: "1121234567",
			imageBase64: "aGVsbG93b3JsZCE=",
			time: Date.now(),
		}, auth).then(result => {
			assertNonEmpty(result.id);
		})
	)
);

// =============================================

const PUBLISH_LOST = `
	mutation PublishLost($itemInfo: ItemInfoInput!) {
		publishLost(itemInfo: $itemInfo) {
			... on LostItemInfo {
				id
			}
			... on LostFoundError {
				error
			}
		}
	}
`;

const publishLost = (itemInfo, userToken) => sendGQL({
	query: PUBLISH_LOST,
	variables: {
		itemInfo: itemInfo,
	},
	auth: userToken
});

unit_test("publish lost", () =>
	after_signUp(auth =>
		publishLost({
			itemName: "qwert",
			description: "qwerty",
			position: "qwertyu",
			contact: "1121234567",
			imageBase64: "aGVsbG93b3JsZCE=",
			time: Date.now(),
		}, auth).then(result => {
			assertNonEmpty(result.id);
		})
	)
);

// =============================================

const MODIFY_LOST_ITEM = `
	mutation ModifyLostItem($lostId: String!, $itemInfo: ItemInfoInput!) {
		modifyLostItem(lostId: $lostId, itemInfo: $itemInfo) {
			... on LostFoundError {
				error
			}
			... on LostItemInfo {
				id
				name
				description
				position
				contact
				lostTime
			}
		}
	}
`;

const modifyLostItem = (lostId, itemInfo, userToken) => sendGQL({
	query: MODIFY_LOST_ITEM,
	variables: {
		lostId: lostId,
		itemInfo,
	},
	auth: userToken
});

unit_test("modify lost item", () =>
	after_publishLost((auth, lostId, oldItem) => {
		const newObj = {
			...oldItem,
			itemName: "modified",
			contact: "12345678910"
		};
		return modifyLostItem(lostId, newObj, auth).then(result => {
			assertEq(result.id, lostId);
			assertEq(result.name, newObj.itemName);
			assertEq(result.description, oldItem.description);
			assertEq(result.position, oldItem.position);
			assertEq(result.contact, newObj.contact);
			assertEq(result.lostTime, oldItem.time);
		});
	})
);

// =============================================

const MODIFY_FOUND_ITEM = `
	mutation ModifyFoundItem($foundId: String!, $itemInfo: ItemInfoInput!) {
		modifyFoundItem(foundId: $foundId, itemInfo: $itemInfo) {
			... on LostFoundError {
				error
			}
			... on FoundItemInfo {
				id
				name
				description
				position
				contact
				foundTime
			}
		}
	}
`;

const modifyFoundItem = (foundId, itemInfo, userToken) => sendGQL({
	query: MODIFY_FOUND_ITEM,
	variables: {
		foundId: foundId,
		itemInfo
	},
	auth: userToken
});

unit_test("modify found item", () =>
	after_publishFound((auth, foundId, oldItem) => {
		const newObj = {
			itemName: "modified",
			contact: "12345678910"
		};
		return modifyFoundItem(foundId, newObj, auth).then(result => {
			assertEq(result.id, foundId)
			assertEq(result.name, newObj.itemName);
			assertEq(result.description, oldItem.description);
			assertEq(result.position, oldItem.position);
			assertEq(result.contact, newObj.contact);
			assertEq(result.foundTime, oldItem.time);
		});
	})
);

// =============================================
const CREATE_POST = `
	mutation CreatePost($postInfo: PostInfoInput!) {
		createPost(postInfo: $postInfo) {
			... on SchoolHeatError {
				error
			}
			... on PostItemInfo {
				postId
			}
		}
	}
`;

const createPost = (postInfo, userToken) => sendGQL({
	query: CREATE_POST,
	variables: {
		postInfo: postInfo,
	},
	auth: userToken
});

unit_test("create post", () => 
	after_signUp(auth => {
		const testPostInfo = {
			title: "nice day",
			content: "have a cup of coffee?",
		};
		return createPost(testPostInfo, auth).then(result => {
			assertNonEmpty(result.postId);
		});
	})
);

// =============================================
const DELETE_POST = `
	mutation DeletePost($postId: String!) {
		deletePost(postId: $postId) {
			... on SchoolHeatError {
				error
			}
			... on PostItemInfo {
				postId
			}
		}
	}
`;

const deletePost = (postId, userToken) => sendGQL({
	query: DELETE_POST,
	variables: {
		postId: postId,
	},
	auth: userToken
});

unit_test("delete post", () => 
	after_n_post_create(1, (auth, postInfos, postIds) => 
		deletePost(postIds[0], auth).then(result =>
			assertEq(result.postId, postIds[0])
		)
	)
);

// =============================================
const CREATE_COMMENT = `
	mutation CreateComment($commentInfo: CommentInfoInput!) {
		createComment(commentInfo: $commentInfo) {
			... on SchoolHeatError {
				error
			}
			... on CommentItemInfo {
				commentId
				content
			}
		}
}
`;

const createComment = (commentInfo, userToken) => sendGQL({
	query: CREATE_COMMENT,
	variables: {
		commentInfo
	},
	auth: userToken
});

unit_test("create comment", () =>
	after_n_post_create(1, (auth, postInfos, postIds) => {
		const commentInfo = {
			postIdToComment: postIds[0],
			content: "lololololo"
		};
		return createComment(commentInfo, auth).then(result => {
			assertNonEmpty(result.commentId);
			assertEq("lololololo", result.content);
		});
	})
);

// =============================================

const CREATE_REPLY = `
	mutation CreateReply($replyInfo: ReplyInfoInput!) {
		createReply(replyInfo: $replyInfo) {
			... on SchoolHeatError {
				error
			}
			... on ReplyItemInfo {
				content
			}
		}
}
`;

const createReply = (replyInfo, userToken) => sendGQL({
	query: CREATE_REPLY,
	variables: {
		replyInfo
	},
	auth: userToken
});

unit_test("create reply", () =>
	after_n_post_create(1, (auth, postInfos, postIds) => {
		const replyInfo = {
			postIdToComment: postIds[0],
			content: "lololololo"
		};
		return createComment(replyInfo, auth).then(result => {
			const commentId = result.commentId;
			
			const replyInfo = {
				commentIdToReply: commentId,
				content: "this is a comment!"
			};
			return createReply(replyInfo, auth).then(result => {
				const content = result.content;
				assertEq(content, replyInfo.content);
			});
		});
	})
);


// =========================================query=========================================

// =============================================
const LOGGED_ID = `
	query LoggedId {
		loggedId {
			... on GetIdError {
				error
			}
			... on GetIdPayload {
				userid
			}
		}
	}
`;
const loggedId = (auth) => {
	return sendGQL({
		query: LOGGED_ID,
		auth: auth,
	});
};

unit_test("logged id", () =>
	after_signUp(auth =>
		loggedId(auth).then(result => {
			assertEq(result.error, undefined);
			assert(result.userid.length > 0);
		})
	)
);
// =============================================

const USER_INFO = `
	query UserInfo($userId: String!) {
		userInfo(userId: $userId) {
			... on PersonalInfoError {
				error
			}
			... on PersonalInfo {
				userId
				pictureUrl
				username
				gender
				grade
				school
				major
				introduction
			}
		}
	}
`;
const userInfo = (userId) => {
	return sendGQL({
		query: USER_INFO,
		variables: {
			userId: userId
		}
	});
};

unit_test("userInfo", () =>
	provided_userId((userId, u, p, nickname) =>
		userInfo(userId).then(result => {
			assertEq(result.error, undefined);
			const expected = {
				userId: userId,
				pictureUrl: "",
				username: nickname,
				gender: "secret",
				grade: "",
				school: "无",
				major: "无",
				introduction: ""
			};
			assert(JSON.stringify(result) == JSON.stringify(expected));
		})
	)
);

// =============================================

const ALL_ACADEMIES = `
	query AllAcademies {
		allAcademies
	}
`;

const allAcademies = () => {
	return sendGQL({
		query: ALL_ACADEMIES,
	});
};

unit_test("allAcademies", () =>
	allAcademies().then(result => {
		assertEq(result.length, 27)
	})
);

// =============================================


const ALL_MAJORS = `
	query AllMajors {
		allMajors
	}
`;

const allMajors = () => {
	return sendGQL({
		query: ALL_MAJORS,
	});
};

unit_test("allMajors", () =>
	allMajors().then(result => {
		assertEq(result.length, 86)
	})
);
// =============================================

const MAJORS_IN = `
	query MajorsIn($academy: String!) {
		majorsIn(academy: $academy) {
			... on MajorsInError {
				error
			}
			... on MajorsInPayload {
				majors
			}
		}
	}
`;

const majorsIn = (academy) => {
	return sendGQL({
		query: MAJORS_IN,
		variables: {
			academy: academy
		},
	});
};

unit_test("majorsIn - invalid academy", () =>
	majorsIn("$%&^%").then(result =>
		assertNonEmpty(result.error)
	)
);

unit_test("majorsIn - valid academy", () =>
	majorsIn("计算机科学与工程学院").then(result => {
		assertEq(result.majors.length, 3)
	})
);

// =============================================
const ALL_LOSTS = `
	query AllLosts($skip: Long, $first: Long) {
		allLosts(skip: $skip, first: $first) {
			totalCount
			lostInfos {
				publisher {
					username
				}
				name
				description
				position
				pictureUrl
				creationTime
				contact
				lostTime
			}
		}
	}
`;

const allLosts = (skip, first) => sendGQL({
	query: ALL_LOSTS,
	variables: {
		skip: skip,
		first: first,
	}
});

unit_test("losts", () =>
	after_n_lost_or_found_publish("lost", 5, (auth, pubItems, pubItemIds) =>
		allLosts(2, 2).then(result => {
			assertEq(result.totalCount, 5);
			const listOfLosts = result.lostInfos;
			const names = listOfLosts.map(x => x.name);
			const originNames = pubItems.slice(2, 4).map(x => x.itemName);
			assertEq(JSON.stringify(names.sort()), JSON.stringify(originNames.sort()));
		})
	)
);

// =============================================
const ALL_FOUNDS = `
	query ALLFounds($skip: Long, $first: Long) {
		allFounds(skip: $skip, first: $first) {
			totalCount
			foundInfos {
				publisher {
					username
				}
				name
				description
				position
				pictureUrl
				creationTime
				contact
				foundTime
			}
		}
	}
`;

const allFounds = (skip, first) => sendGQL({
	query: ALL_FOUNDS,
	variables: {
		skip: skip,
		first: first,
	}
});

unit_test("founds", () =>
	after_n_lost_or_found_publish("found", 5, (auth, pubItems, pubItemIds) =>
		allFounds(2, 2).then(result => {
			assertEq(result.totalCount, 5);
			const listOfFounds = result.foundInfos;
			const names = listOfFounds.map(x => x.name);
			const originNames = pubItems.slice(2, 4).map(x => x.itemName);
			assertEq(JSON.stringify(names.sort()), JSON.stringify(originNames.sort()));
		})
	)
);

// =============================================

const FOUND_ITEM_INFO = `
	query FoundItemInfo($foundId: String!) {
		foundItemInfo(foundId: $foundId) {
			... on FoundItemInfo {
				publisher {
					username
				}
				name
				description
				position
				pictureUrl
				creationTime
				contact
				foundTime
			}
			... on LostFoundError {
				error
			}
		}
	}
`;

const foundItemInfo = (foundId) => sendGQL({
	query: FOUND_ITEM_INFO,
	variables: {
		foundId: foundId,
	},
});

unit_test("found item info", () =>
	after_signUp(auth => {
		const testObj = {
			itemName: "qwert",
			description: "qwerty",
			position: "qwertyu",
			contact: "1121234567",
			imageBase64: "aGVsbG93b3JsZCE=",
			time: Date.now(),
		};
		return publishFound(testObj, auth).then(result => {
			const itemId = result.id;
			return foundItemInfo(itemId).then(result => {
				assertEq(result.name, testObj.itemName);
				assertEq(result.description, testObj.description);
				assertEq(result.position, testObj.position);
				assertEq(result.contact, testObj.contact);
				assertEq(result.foundTime, testObj.time);
			});
		});
	})
);

// =============================================

const LOST_ITEM_INFO = `
	query LostItemInfo($lostId: String!) {
		lostItemInfo(lostId: $lostId) {
			... on LostItemInfo {
				publisher {
					username
				}
				name
				description
				position
				pictureUrl
				creationTime
				contact
				lostTime
			}
			... on LostFoundError {
				error
			}
		}
	}
`;

const lostItemInfo = (lostId) => sendGQL({
	query: LOST_ITEM_INFO,
	variables: {
		lostId: lostId,
	},
});

unit_test("lost item info", () =>
	after_signUp(auth => {
		const testObj = {
			itemName: "qwert",
			description: "qwerty",
			position: "qwertyu",
			contact: "1121234567",
			imageBase64: "aGVsbG93b3JsZCE=",
			time: Date.now(),
		};
		return publishLost(testObj, auth).then(result => {
			const itemId = result.id;
			return lostItemInfo(itemId).then(result => {
				assertEq(result.name, testObj.itemName);
				assertEq(result.description, testObj.description);
				assertEq(result.position, testObj.position);
				assertEq(result.contact, testObj.contact);
				assertEq(result.lostTime, testObj.time);
			});
		});
	})
);

// =============================================
// 注: commentTo === null时表示回复层主，非空时表示回复某个userId
// SortedBy = {TimeAsc | TimeDes | HeatAsc | HeatDes}
const ALL_POSTS = `
	query AllPosts($skip: Long, $first: Long, $sortedBy: SortedBy) {
		allPosts(skip: $skip, first: $first, sortedBy: $sortedBy) {
			postInfos {
				postId
				title
					content
					author {
					username
				}
				latestCommenter {
					username
				}
				latestActiveTime
				createTime
				heat
				allComments {
					comments {
						commentId
						content
						author {
							username
						}
						allReplies {
							content
							replyTo {
								username
							}
							author {
								username
							}
						}
					}
				}
			}
		}
	}
`;

const allPosts = (skip, first, sortedBy) => sendGQL({
	query: ALL_POSTS,
	variables: {
		skip: skip,
		first: first,
		sortedBy: sortedBy
	}
});

unit_test("all posts", () => 
	after_n_post_create(10, (auth, postInfos, postIds) =>
		allPosts(0, 10, "TimeAsc").then(result => {
			const resultPostIds = result.postInfos.map(r => r.postId);
			const originPostIds = postIds;
			assertEq(JSON.stringify(resultPostIds), JSON.stringify(originPostIds));
		})
	)
);

// =============================================
const POST_INFO = `
	query PostInfo($postId: String!) {
		postInfo(postId: $postId) {
			... on SchoolHeatError {
				error
			}
			... on PostItemInfo {
				postId
				title
				content
				allComments {
					comments {
						commentId
						content
						author {
							username
						}
						allReplies {
							content
							replyTo {
								username
							}
							author {
								username
							}
						}
					}
				}
			}
		}
	}
`;

const postInfo = (postId) => sendGQL({
	query: POST_INFO,
	variables: {
		postId: postId
	}
});

unit_test("post info", () => 
	after_n_post_create(1, (auth, postInfos, postIds) => 
		postInfo(postIds[0]).then(result => {
			const originPostInfo = postInfos[0];
			assertEq(result.postId, postIds[0]);
			assertEq(result.title, originPostInfo.title);
			assertEq(result.content, originPostInfo.content);
		})
	)
);

// =========================================use case========================================
// =========================================use case========================================
// =========================================use case========================================

function isCanvasBlank(canvas) {
	return !canvas.getContext('2d')
		.getImageData(0, 0, canvas.width, canvas.height).data
		.some(channel => channel !== 0);
}

behav_test_only("upload user profile image and then get url", () =>
	after_signUp(auth => {
		const base64Image = getBase64Image(document.getElementById("test-img"));
		return uploadUserProfile(base64Image, auth).then(result => {
			assert(result.ok);
			return loggedId(auth).then(result => {
				const userId = result.userid;
				return userInfo(userId).then(result => {
					const relativePath = result.pictureUrl;
					const canvas = document.getElementById("canvas");
					const context = canvas.getContext('2d');
					const image = new Image();
					image.src = remoteAddress + relativePath;
					image.crossOrigin = "anonymous";
					image.onload = function () {
						context.drawImage(image, 0, 0);
						assert(!isCanvasBlank(canvas));
					};
				});
			});
		});
	})
);

fire_all_tests();
