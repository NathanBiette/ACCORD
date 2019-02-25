function[W,H] = autre_test_de_NMF(V,K,Maxiter)

F = size(V,1);
T = size(V,2);

rand('seed',0)
W = 1+ rand(F, K);
H = 1+ rand(K, T);

ONES = ones(F,T);
for i = 1 : Maxiter
% mise à jour de H : activations 
  H= H .*(W'*(V./(W*H+eps)))./(W'*ONES);
%mise à jour de W : dictionnaires
  W = W. *((V./(W*H+eps))*H')./(ONES*H');
end;

% normaliser W 
sumW= sum(W);
W = W* diag(1./sumW);
H = diag(sumW)*H;
