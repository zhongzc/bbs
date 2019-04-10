const sendGQL = (queryPayloadObject) => {
	return fetch("http://localhost:4000/graphql/", {
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


//---------unit test---------

let unitTests = [];
const unit_test = (name, f) => {
	unitTests.push({ name: name, func: f });
};
const unit_test_only = (name, f) => {
	unitTests.push({ name: name, func: f, unique: true });
}

const fire_unit_test = async () => {
	const uniqueUnitTest = unitTests.filter(testObj => testObj.unique !== undefined);
	if (uniqueUnitTest.length > 1) {
		console.error("===== more than one unique unit test found =====");
		return;
	}
	const testsToRun = uniqueUnitTest.length === 0 ? unitTests : uniqueUnitTest;
	let passTestCounter = 0;
	for (let f of testsToRun) {
		await f.func()
			.then(() => {
				passTestCounter++;
				return console.log("SUCCESS # unit test: " + f.name)
			})
			.then(() => new Promise(resolve => setTimeout(resolve, 30)))  // visual enjoyment
			.catch(error => {
				console.error("=== (" + f.name + ") unit test FAILED ===");
				console.error(error);
			});
	}
	console.log("Unit tests Pass/Run: " + passTestCounter + "/" + testsToRun.length);
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

const fail = () => {
	throw new Error("test failed");
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
				ok
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
			assert(result.ok);
		});
	})
);

// =============================================

const CHANGE_GENDER = `
	mutation ChangeGender($gender: String!) {
		changeGender(gender: $gender) {
			... on ModifyPersonInfoSuccess {
				ok
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
			assert(result.ok);
		})
	})
);
// =============================================

const CHANGE_GRADE = `
	mutation ChangeGrade($grade: String!) {
		changeGrade(grade: $grade) {
			... on ModifyPersonInfoSuccess {
				ok
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
			assert(result.ok);
		})
	})
);
// =============================================

const CHANGE_INTRODUCTION = `
	mutation ChangeIntroduction($introduction: String!) {
		changeIntroduction(introduction: $introduction) {
			... on ModifyPersonInfoSuccess {
				ok
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
			assert(result.ok);
		})
	})
);
// =============================================

const CHANGE_NICKNAME = `
	mutation ChangeNickname($nickname: String!) {
		changeNickname(nickname: $nickname) {
			... on ModifyPersonInfoSuccess {
				ok
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
			assert(result.ok);
		})
	})
);
// =============================================

const CHANGE_ACADEMY = `
	mutation ChangeAcademy($academy: String!) {
		changeAcademy(academy: $academy) {
			... on ModifyPersonInfoSuccess {
				ok
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
			assert(result.ok);
		})
	})
);
// =============================================

const CHANGE_MAJOR = `
	mutation ChangeMajor($major: String!) {
		changeMajor(major: $major) {
			... on ModifyPersonInfoSuccess {
				ok
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
			assert(result.ok);
		})
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
			const exceptedResult = {
				pictureUrl: "",
				username: nickname,
				gender: "secret",
				grade: "",
				school: "无",
				major: "无",
				introduction: ""
			};
			assert(JSON.stringify(result) == JSON.stringify(exceptedResult));
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



fire_unit_test();
// =========================================use case========================================
// =========================================use case========================================
// =========================================use case========================================
