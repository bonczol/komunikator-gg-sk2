#pragma once
#include <string>
#include <ctime>
#include <list>
#include "Klient.h"

class Message{
private:
	std::string msg;
	std::string date;
	std::string time;
	Klient* sender;

public:
	Message(std::string msg, Klient* sender);
	Message(){}
	std::string getMsg() { return this->msg; }
	std::string toString1(int id_conv);
	std::string toString2();
	~Message();


	template<class Archive>
	void serialize(Archive & ar, const unsigned int version)
	{
		ar & msg;
		ar & date;
		ar & time;
		ar & sender;
	}
};

